package org.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.entity.Rotation;
import org.shop.entity.dto.ProdLocateDTO;
import org.shop.entity.dto.RotationAllDTO;


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
