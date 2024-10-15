package org.shop.supply.mapper.repo;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.shop.supply.entity.Prod;
import org.shop.supply.mapper.ProdMapper;
import org.springframework.stereotype.Component;

/**
 * 抽取DAO层
 */
@Component
@RequiredArgsConstructor
public class SupplyRepo {

    private final ProdMapper prodMapper;


    public Prod findByProdName_UserId(String name, Long id) {
        return prodMapper.selectOne(Wrappers.<Prod>lambdaQuery().eq(Prod::getName, name).eq(Prod::getUserId, id));
    }
}
