package org.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.entity.ProdFunc;
import org.shop.mapper.ProdFuncMapper;
import org.shop.service.ProdFuncService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdFuncServiceImpl extends ServiceImpl<ProdFuncMapper, ProdFunc> implements ProdFuncService {
}
