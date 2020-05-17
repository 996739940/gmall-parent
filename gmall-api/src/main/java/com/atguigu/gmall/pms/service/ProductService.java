package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.to.es.EsProduct;
import com.atguigu.gmall.vo.PageInfoVo;
import com.atguigu.gmall.vo.product.PmsProductParam;
import com.atguigu.gmall.vo.product.PmsProductQueryParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-04-10
 */
public interface ProductService extends IService<Product> {

    /**
     * 查询商品详情
     * @param id
     * @return
     */
    Product productInfo(Long id);

    /**
     * @Author 张燕廷
     * @Description 根据复杂查询条件返回分页数据
     * @Date 8:34 2020/4/14
     * @Param [productQueryParam]
     * @return com.atguigu.gmall.vo.PageInfoVo
     **/
    PageInfoVo productPageInfo(PmsProductQueryParam productQueryParam);

    void saveProduct(PmsProductParam productParam);

    void updatePublishStatus(List<Long> ids, Integer publishStatus);

    EsProduct productAllInfo(Long id);

    EsProduct produSkuInfo(Long id);
}
