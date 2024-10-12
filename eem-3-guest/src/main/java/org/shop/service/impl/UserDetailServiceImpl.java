package org.shop.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.shop.entity.UserDetail;
import org.shop.mapper.UserDetailMapper;
import org.shop.service.UserDetailService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements UserDetailService {
}
