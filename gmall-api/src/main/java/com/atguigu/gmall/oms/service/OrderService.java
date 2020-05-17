package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.oms.entity.Order;
import com.atguigu.gmall.vo.order.OrderConfirmVo;
import com.atguigu.gmall.vo.order.OrderCreateVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-04-10
 */
public interface OrderService extends IService<Order> {

    OrderConfirmVo orderConfirm(Long id);

    OrderCreateVo createOrder(BigDecimal totalPrice, Long addressId, String note);

    String resolvePayResult(Map<String, String> params);

    String pay(String orderSn, String accessToken);
}
