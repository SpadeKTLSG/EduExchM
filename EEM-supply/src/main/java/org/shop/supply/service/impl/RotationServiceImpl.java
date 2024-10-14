package org.shop.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.entity.Prod;
import org.shop.supply.entity.Rotation;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.dto.RotationAllDTO;
import org.shop.supply.mapper.RotationMapper;
import org.shop.supply.service.ProdService;
import org.shop.supply.service.RotationService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RotationServiceImpl extends ServiceImpl<RotationMapper, Rotation> implements RotationService {

    private ProdService prodService;
    public void setProdService(@Lazy ProdService prodService) {
        this.prodService = prodService;
    }


    @Override
    public void add2Rotation(RotationAllDTO rotationAllDTO) {
        Rotation rotation = new Rotation();
        BeanUtils.copyProperties(rotationAllDTO, rotation);
        this.save(rotation);

    }

    @Override
    public void add2Rotation(ProdLocateDTO prodLocateDTO) {

        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));

        RotationAllDTO rotationAllDTO = RotationAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .build();

        this.add2Rotation(rotationAllDTO);
    }


    @Override
    public void remove4Rotation(RotationAllDTO rotationAllDTO) {

        Rotation rotation = this.getOne(new LambdaQueryWrapper<Rotation>()
                .eq(Rotation::getProdId, rotationAllDTO.getProdId()));

        this.removeById(rotation);
    }


    @Override
    public void remove4Rotation(ProdLocateDTO prodLocateDTO) {

        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));

        RotationAllDTO rotationAllDTO = RotationAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .build();

        this.remove4Rotation(rotationAllDTO);
    }
}
