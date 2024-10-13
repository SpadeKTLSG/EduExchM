package org.shop.supply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.supply.entity.ProdCate;
import org.shop.supply.entity.dto.ProdCateAllDTO;


public interface ProdCateService extends IService<ProdCate> {
    // 查阅主表 ProdService

    /**
     * 保存分类
     */
    void postCateA(ProdCateAllDTO prodCateAllDTO);

    /**
     * 删除分类
     */
    void deleteCateA(ProdCateAllDTO prodCateAllDTO);

    /**
     * 更新分类
     */
    void putCateA(ProdCateAllDTO prodCateAllDTO);
}
