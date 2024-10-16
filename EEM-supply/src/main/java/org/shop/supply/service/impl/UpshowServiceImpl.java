package org.shop.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.entity.Prod;
import org.shop.supply.entity.Upshow;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.dto.UpshowAllDTO;
import org.shop.supply.mapper.UpshowMapper;
import org.shop.supply.service.ProdService;
import org.shop.supply.service.UpshowService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UpshowServiceImpl extends ServiceImpl<UpshowMapper, Upshow> implements UpshowService {

    private ProdService prodService;
    public void setProdService(@Lazy ProdService prodService) {
        this.prodService = prodService;
    }


    @Override
    public void add2Upshow(UpshowAllDTO upshowAllDTO) {
        Upshow upshow = new Upshow();
        BeanUtils.copyProperties(upshowAllDTO, upshow);
        this.save(upshow);
    }


    @Override
    public void add2Upshow(ProdLocateDTO prodLocateDTO) {

        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));

        UpshowAllDTO rotationDTO = UpshowAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .build();

        this.add2Upshow(rotationDTO);
    }


    @Override
    public void remove4Upshow(UpshowAllDTO upshowAllDTO) {

        Upshow upshow = this.getOne(new LambdaQueryWrapper<Upshow>()
                .eq(Upshow::getProdId, upshowAllDTO.getProdId()));

        this.removeById(upshow);
    }


    @Override
    public void remove4Upshow(ProdLocateDTO prodLocateDTO) {

        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));

        UpshowAllDTO rotationDTO = UpshowAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .build();

        this.remove4Upshow(rotationDTO);
    }


}
