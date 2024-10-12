package org.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.entity.Hotsearch;
import org.shop.entity.dto.HotsearchAllDTO;
import org.shop.entity.dto.ProdLocateDTO;


public interface HotsearchService extends IService<Hotsearch> {

    /**
     * 添加一条热搜
     */
    void add2Hotsearch(HotsearchAllDTO hotsearchAllDTO);

    /**
     * 用商品添加一条热搜
     */
    void add2Hotsearch(ProdLocateDTO prodLocateDTO);

    /**
     * 删除一条热搜
     */
    void remove4Hotsearch(HotsearchAllDTO hotsearchAllDTO);

    /**
     * 用商品删除一条热搜
     */
    void remove4Hotsearch(ProdLocateDTO prodLocateDTO);

    /**
     * 清空所有热搜
     */
    void clearAllHotsearch();
}
