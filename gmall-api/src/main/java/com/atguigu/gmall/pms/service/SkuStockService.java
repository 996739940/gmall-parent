package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.entity.SkuStock;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * sku 服务类
 * </p>
 *
 * @author Lfy
 * @since 2020-04-10
 */
public interface SkuStockService extends IService<SkuStock> {

    BigDecimal getSkuPriceBySkuId(Long skuId);
}
