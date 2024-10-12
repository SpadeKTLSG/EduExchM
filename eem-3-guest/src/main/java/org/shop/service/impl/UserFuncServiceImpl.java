package org.shop.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.shop.entity.UserFunc;
import org.shop.mapper.UserFuncMapper;
import org.shop.service.UserFuncService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserFuncServiceImpl extends ServiceImpl<UserFuncMapper, UserFunc> implements UserFuncService {
}
