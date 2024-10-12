package org.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.entity.Upshow;
import org.shop.entity.dto.ProdLocateDTO;
import org.shop.entity.dto.UpshowAllDTO;


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
