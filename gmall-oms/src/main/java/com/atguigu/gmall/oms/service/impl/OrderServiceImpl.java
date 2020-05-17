package com.atguigu.gmall.oms.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.cart.vo.CartItem;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.constant.OrderStatusEnume;
import com.atguigu.gmall.constant.SysCacheConstant;
import com.atguigu.gmall.oms.component.MemberComponent;
import com.atguigu.gmall.oms.config.AlipayConfig;
import com.atguigu.gmall.oms.entity.Order;
import com.atguigu.gmall.oms.entity.OrderItem;
import com.atguigu.gmall.oms.mapper.OrderItemMapper;
import com.atguigu.gmall.oms.mapper.OrderMapper;
import com.atguigu.gmall.oms.service.OrderService;
import com.atguigu.gmall.pms.entity.SkuStock;
import com.atguigu.gmall.pms.service.ProductService;
import com.atguigu.gmall.pms.service.SkuStockService;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.to.es.EsProductAttributeValue;
import com.atguigu.gmall.to.es.EsSkuProductInfo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberReceiveAddress;
import com.atguigu.gmall.ums.service.MemberService;
import com.atguigu.gmall.vo.order.OrderConfirmVo;
import com.atguigu.gmall.vo.order.OrderCreateVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2020-04-10
 */
@Slf4j
@Service
@Component
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Reference
    MemberService memberService;

    @Reference
    CartService cartService;

    @Reference
    ProductService productService;

    @Reference
    SkuStockService skuStockService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberComponent memberComponent;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    ThreadLocal<List<CartItem>> threadLocal = new ThreadLocal<>();



    @Override
    public OrderConfirmVo orderConfirm(Long id) {

        //1、获取上一步隐式传参带来的accessToken
        String accessToken = RpcContext.getContext().getAttachment("accessToken");
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        //会员收货地址
        confirmVo.setAddresses(memberService.getMemberAddress(id));


        //设置优惠卷信息
        confirmVo.setCoupons(null);


        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        //设置购物项
        confirmVo.setItems(cartItems);


        String token = UUID.randomUUID().toString().replace("-", "");
        //给令牌加上业务的过期时间
        token = token+"_"+System.currentTimeMillis()+"_"+10*60*1000;
        //保存防重令牌
        redisTemplate.opsForSet().add(SysCacheConstant.ORDER_UNIQUE_TOKEN,token);
        //设置订单的防重令牌
        confirmVo.setOrderToken(token);

        //运费是远程计算的
        confirmVo.setTransPrice(new BigDecimal("10"));

        //计算价格等
        confirmVo.setCouponPrice(null);
        cartItems.forEach((cartItem)->{
            Integer count = cartItem.getCount();
            confirmVo.setCount(confirmVo.getCount()+count);
            BigDecimal totalPrice = cartItem.getTotalPrice();
            confirmVo.setProductTotalPrice(confirmVo.getProductTotalPrice().add(totalPrice));
        });


        confirmVo.setTotalPrice(confirmVo.getProductTotalPrice().add(confirmVo.getTransPrice()));
        return confirmVo;
    }


    @Transactional
    @Override
    public OrderCreateVo createOrder(BigDecimal frontTotalPrice, Long addressId, String note) {

        //0、防重复；
        //禁止传以下参数：
        /**
         * token,timeout,retires,xxxxx；dubbo标签的所有属性都是关键字不能隐式传参。
         */
        String orderToken = RpcContext.getContext().getAttachment("orderToken");

        //验证令牌的第一种失败
        if(StringUtils.isEmpty(orderToken)){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("此次操作出现错误，请重新尝试");
            return orderCreateVo;
        }


        //令牌合法性   token = token+"_"+System.currentTimeMillis()+"_"+60*10;
        String[] s = orderToken.split("_");
        if(s.length != 3){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("非法的操作，请重试");
            return orderCreateVo;
        }


        //令牌超时验证
        long createTime = Long.parseLong(s[1]);
        long timeout = Long.parseLong(s[2]);
        if(System.currentTimeMillis()-createTime >= timeout){
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("页面超时，请刷新");
            return orderCreateVo;
        }

        //验证重复
        Long remove = redisTemplate.opsForSet().remove(SysCacheConstant.ORDER_UNIQUE_TOKEN, orderToken);
        if(remove == 0){
            //令牌非法
            OrderCreateVo orderCreateVo = new OrderCreateVo();
            orderCreateVo.setToken("创建失败，请刷新重试");
            return orderCreateVo;
        }


        //1、获取到当前会员
        String accessToken = RpcContext.getContext().getAttachment("accessToken");

        //订单验价，对了就是true，错了false
        Boolean vaildPrice = vaildPrice(frontTotalPrice, accessToken, addressId);
        if(!vaildPrice){
            OrderCreateVo createVo = new OrderCreateVo();
            createVo.setLimit(false);//比价失败
            createVo.setToken("订单金额发生变化，请重新提交");
            return createVo;
        }


        Member member = memberComponent.getMemberByAccessToken(accessToken);

        //初始化前端订单vo数据
        OrderCreateVo orderCreateVo = initOrderCreateVo(frontTotalPrice, addressId, accessToken, member);


        //初始化数据库订单信息
        Order order = initOrder(frontTotalPrice, addressId, note, orderCreateVo, member);
        //保存订单；数据库幂等。保存的时候，幂等字段需要唯一索引；
        orderMapper.insert(order);

        //2、构造/保存订单项；ThreadLocal同一个线程共享数据
        saveOrderItem(order,accessToken);


        return orderCreateVo;
    }

    @Override
    public String pay(String orderSn, String accessToken) {

        Order order = orderMapper.selectOne(new QueryWrapper<Order>().eq("order_sn", orderSn));
        List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_sn", orderSn));

        String productName = orderItems.get(0).getProductName();
        StringBuffer body = new StringBuffer();
        for(OrderItem item:orderItems){
            body.append(item.getProductName());
        }

        //调用支付宝的支付方法，会返回一个支付页
        String result = payOrder(orderSn, order.getTotalAmount().toString(), "【谷粒商城】-" + productName, body.toString());
        return result;
    }

    @Override
    public String resolvePayResult(Map<String, String> params) {

        boolean signVerified = true;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset,
                    AlipayConfig.sign_type);
            System.out.println("验签：" + signVerified);

        } catch (AlipayApiException e) {

        }
        // 商户订单号
        String out_trade_no = params.get("out_trade_no");
        // 支付宝流水号
        String trade_no = params.get("trade_no");
        // 交易状态
        String trade_status = params.get("trade_status");


        //只要支付成功，支付宝立即通知，5s,1min,3min,
        if (trade_status.equals("TRADE_FINISHED")) {
            //改订单状态
            log.debug("订单【{}】,已经完成...不能再退款。数据库都改了",out_trade_no);

        } else if (trade_status.equals("TRADE_SUCCESS")) {
            //改数据的订单状态
            Order order = new Order();
            order.setStatus(OrderStatusEnume.PAYED.getCode());
            orderMapper.update(order,new UpdateWrapper<Order>().eq("order_sn",out_trade_no));
            log.debug("订单【{}】,已经支付成功...可以退款。数据库都改了",out_trade_no);
        }

        return "success";

    }

    private void saveOrderItem(Order order,String accessToken) {

        List<Long> skuIds = new ArrayList<>();
        List<CartItem> cartItems = threadLocal.get();

        List<OrderItem> orderItems = new ArrayList<>();

        cartItems.forEach((cartItem)->{
            skuIds.add(cartItem.getSkuId());
            OrderItem orderItem = new OrderItem();

            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
            Long skuId = cartItem.getSkuId();
            //查询当前skuId对应的商品信息
            EsProduct esProduct = productService.produSkuInfo(skuId);
            List<EsSkuProductInfo> skuProductInfos = esProduct.getSkuProductInfos();
            SkuStock skuStock = new SkuStock();
            String attValuejsonStr = "";
            for (EsSkuProductInfo skuProductInfo:skuProductInfos){
                if(skuId.equals(skuProductInfo.getId())){
                    List<EsProductAttributeValue> values = skuProductInfo.getAttributeValues();
                    attValuejsonStr = JSON.toJSONString(values);
                    BeanUtils.copyProperties(skuProductInfo,skuStock);
                }

            }
            orderItem.setProductId(esProduct.getId());
            orderItem.setProductPic(esProduct.getPic());
            orderItem.setProductName(esProduct.getName());
            orderItem.setProductBrand(esProduct.getBrandName());
            orderItem.setProductSn(esProduct.getProductSn());
            //当前购物项的价格；
            orderItem.setProductPrice(cartItem.getPrice());
            orderItem.setProductQuantity(cartItem.getCount());
            orderItem.setProductSkuId(skuId);
            orderItem.setProductSkuCode(skuStock.getSkuCode());
            orderItem.setProductCategoryId(esProduct.getProductCategoryId());
            orderItem.setSp1(skuStock.getSp1());
            orderItem.setSp2(skuStock.getSp2());
            orderItem.setSp3(skuStock.getSp3());
            orderItem.setProductAttr(attValuejsonStr);
            orderItems.add(orderItem);
            orderItemMapper.insert(orderItem);


        });

        //3、清除购物车中已经下单的商品
        cartService.removeCartItem(accessToken,skuIds);
    }

    /**
     * 构造订单vo
     * @param frontTotalPrice
     * @param addressId
     * @param accessToken
     * @param member
     * @return
     */
    private OrderCreateVo initOrderCreateVo(BigDecimal frontTotalPrice, Long addressId, String accessToken, Member member) {
        String timeId = IdWorker.getTimeId();

        OrderCreateVo orderCreateVo = new OrderCreateVo();

        //设置订单号
        orderCreateVo.setOrderSn(timeId);
        //设置收货地址
        orderCreateVo.setAddressId(addressId);
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        //设置购物车中的数据
        orderCreateVo.setCartItems(cartItems);

        //设置会员id
        orderCreateVo.setMemberId(member.getId());
        //总价格
        orderCreateVo.setTotalPrice(frontTotalPrice);
        //描述信息
        orderCreateVo.setDetailInfo(cartItems.get(0).getName());
        return orderCreateVo;
    }

    private Order initOrder(BigDecimal frontTotalPrice, Long addressId, String note, OrderCreateVo orderCreateVo, Member member) {
        //加工处理数据；
        //1、保存订单信息
        Order order = new Order();
        order.setMemberId(member.getId());
        order.setOrderSn(orderCreateVo.getOrderSn());
        order.setCreateTime(new Date());
        order.setAutoConfirmDay(7);
        //order.setBillContent()
        order.setNote(note);
        order.setMemberUsername(member.getUsername());

        //订单总额
        order.setTotalAmount(frontTotalPrice);
        order.setFreightAmount(new BigDecimal("10.00"));
        order.setStatus(OrderStatusEnume.UNPAY.getCode());

        //设置收货人信息
        MemberReceiveAddress address = memberService.getMemberAddressByAddressId(addressId);
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverCity(address.getCity());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverDetailAddress(address.getDetailAddress());

        return order;
    }



    private Boolean vaildPrice(BigDecimal frontPrice,String accessToken,Long addressId){


        //1、拿到购物车
        List<CartItem> cartItems = cartService.getCartItemForOrder(accessToken);
        threadLocal.set(cartItems);
        BigDecimal bigDecimal = new BigDecimal("0");

        //我们的总价必须去库存服务查出最新价格；
        for (CartItem item:cartItems){
            //bigDecimal = bigDecimal.add(item.getTotalPrice());
            //BigDecimal price = item.getPrice();
            //查出真正的价格
            Long skuId = item.getSkuId();
            BigDecimal newPrice = skuStockService.getSkuPriceBySkuId(skuId);
            item.setPrice(newPrice);
            Integer count = item.getCount();
            //当前项的总价
            BigDecimal multiply = newPrice.multiply(new BigDecimal(count.toString()));
            bigDecimal = bigDecimal.add(multiply);

        }

        //2、根据收货地址计算运费
        BigDecimal tranPrice = new BigDecimal("10");


        BigDecimal totalPrice = bigDecimal.add(tranPrice);



        return totalPrice.compareTo(frontPrice)==0?true:false;
    }


    /**
     * 支付方法
     * @param out_trade_no   订单号
     * @param total_amount   总金额
     * @param subject        标题
     * @param body           描述
     * @return
     */
    private String payOrder(String out_trade_no,
                            String total_amount,
                            String subject,
                            String body) {
        // 1、创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl,
                AlipayConfig.app_id,
                AlipayConfig.merchant_private_key, "json",
                AlipayConfig.charset,
                AlipayConfig.alipay_public_key,
                AlipayConfig.sign_type);

        // 2、创建一次支付请求
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        // 商户订单号，商户网站订单系统中唯一订单号，必填
        // 付款金额，必填
        // 订单名称，必填
        // 商品描述，可空

        // 3、构造支付请求数据
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"," + "\"total_amount\":\"" + total_amount
                + "\"," + "\"subject\":\"" + subject + "\"," + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = "";
        try {
            // 4、请求
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;// 支付跳转页的代码

    }


}

