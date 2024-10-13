package org.shop.supply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.supply.entity.Upshow;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.dto.UpshowAllDTO;


public interface UpshowService extends IService<Upshow> {

    /**
     * 添加到Upshow
     */
    void add2Upshow(UpshowAllDTO upshowAllDTO);

    /**
     * 添加到Upshow
     */
    void add2Upshow(ProdLocateDTO prodLocateDTO);

    /**
     * 从Upshow中移除
     */
    void remove4Upshow(UpshowAllDTO upshowAllDTO);

    /**
     * 从Upshow中移除
     */
    void remove4Upshow(ProdLocateDTO prodLocateDTO);


}
