package org.shop.supply.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.MessageConstant;
import org.shop.common.exception.SthHasCreatedException;
import org.shop.common.exception.SthNotFoundException;
import org.shop.supply.entity.ProdCate;
import org.shop.supply.entity.dto.ProdCateAllDTO;
import org.shop.supply.mapper.ProdCateMapper;
import org.shop.supply.service.ProdCateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProdCateServiceImpl extends ServiceImpl<ProdCateMapper, ProdCate> implements ProdCateService {

    @Override
    public void postCateA(ProdCateAllDTO prodCateAllDTO) {

        if (this.getOne(new LambdaQueryWrapper<ProdCate>()
                .eq(ProdCate::getName, prodCateAllDTO.getName()), false) != null) throw new SthHasCreatedException(MessageConstant.OBJECT_HAS_ALIVE);

        this.save(ProdCate.builder()
                .name(prodCateAllDTO.getName())
                .description(prodCateAllDTO.getDescription())
                .build());
    }

    @Override
    public void deleteCateA(ProdCateAllDTO prodCateAllDTO) {

        this.remove(new LambdaQueryWrapper<ProdCate>()
                .eq(ProdCate::getName, prodCateAllDTO.getName()));

    }


    @Override
    public void putCateA(ProdCateAllDTO prodCateAllDTO) {
        ProdCate prodCate = this.getOne(new LambdaQueryWrapper<ProdCate>()
                .eq(ProdCate::getName, prodCateAllDTO.getName()), false);
        if (prodCate == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        BeanUtils.copyProperties(prodCateAllDTO, prodCate);
        this.updateById(prodCate);
    }


}
