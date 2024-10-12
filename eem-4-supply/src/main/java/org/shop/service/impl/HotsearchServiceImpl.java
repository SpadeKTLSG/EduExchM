package org.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.shop.entity.Hotsearch;
import org.shop.entity.Prod;
import org.shop.entity.ProdFunc;
import org.shop.entity.dto.HotsearchAllDTO;
import org.shop.entity.dto.ProdLocateDTO;
import org.shop.mapper.HotsearchMapper;
import org.shop.service.HotsearchService;
import org.shop.service.ProdFuncService;
import org.shop.service.ProdService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class HotsearchServiceImpl extends ServiceImpl<HotsearchMapper, Hotsearch> implements HotsearchService {

    @Autowired
    private ProdService prodService;
    @Autowired
    private ProdFuncService prodFuncService;


    @Override
    public void add2Hotsearch(HotsearchAllDTO hotsearchAllDTO) {
        Hotsearch hotsearch = new Hotsearch();
        BeanUtils.copyProperties(hotsearchAllDTO, hotsearch);
        this.save(hotsearch);
    }


    @Override
    public void add2Hotsearch(ProdLocateDTO prodLocateDTO) {
        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));
        ProdFunc prodFunc = prodFuncService.getById(prod2Get.getId());

        HotsearchAllDTO hotsearchAllDTO = HotsearchAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .visit(prodFunc.getVisit())
                .build();
        this.add2Hotsearch(hotsearchAllDTO);
    }


    @Override
    public void remove4Hotsearch(HotsearchAllDTO hotsearchAllDTO) {

        Hotsearch hotsearch = this.getOne(new LambdaQueryWrapper<Hotsearch>()
                .eq(Hotsearch::getName, hotsearchAllDTO.getName())
                .eq(Hotsearch::getProdId, hotsearchAllDTO.getProdId()));
        this.removeById(hotsearch);
    }


    @Override
    public void remove4Hotsearch(ProdLocateDTO prodLocateDTO) {
        Prod prod2Get = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));
        ProdFunc prodFunc = prodFuncService.getById(prod2Get.getId());

        HotsearchAllDTO hotsearchAllDTO = HotsearchAllDTO.builder()
                .prodId(prod2Get.getId())
                .name(prod2Get.getName())
                .visit(prodFunc.getVisit())
                .build();
        this.add2Hotsearch(hotsearchAllDTO);
    }


    @Override
    public void clearAllHotsearch() {
        this.remove(null);
        log.debug("清空热搜成功");
    }
}
