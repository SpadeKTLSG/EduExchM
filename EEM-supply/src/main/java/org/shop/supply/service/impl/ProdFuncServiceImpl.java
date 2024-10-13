package org.shop.supply.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.entity.ProdFunc;
import org.shop.supply.mapper.ProdFuncMapper;
import org.shop.supply.service.ProdFuncService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdFuncServiceImpl extends ServiceImpl<ProdFuncMapper, ProdFunc> implements ProdFuncService {
}
