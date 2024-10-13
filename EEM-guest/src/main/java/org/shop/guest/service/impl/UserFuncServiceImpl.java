package org.shop.guest.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.guest.entity.UserFunc;
import org.shop.guest.mapper.UserFuncMapper;
import org.shop.guest.service.UserFuncService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFuncServiceImpl extends ServiceImpl<UserFuncMapper, UserFunc> implements UserFuncService {
}
