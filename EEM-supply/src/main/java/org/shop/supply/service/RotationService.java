package org.shop.supply.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.supply.entity.Rotation;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.dto.RotationAllDTO;


public interface RotationService extends IService<Rotation> {

    /**
     * 添加到Rotation
     */
    void add2Rotation(RotationAllDTO rotationAllDTO);

    /**
     * 添加到Rotation
     */
    void add2Rotation(ProdLocateDTO prodLocateDTO);

    /**
     * 从Rotation中移除
     */
    void remove4Rotation(RotationAllDTO rotationAllDTO);

    /**
     * 从Rotation中移除
     */
    void remove4Rotation(ProdLocateDTO prodLocateDTO);
}
