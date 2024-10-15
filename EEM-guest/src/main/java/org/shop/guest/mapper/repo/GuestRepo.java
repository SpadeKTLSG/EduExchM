package org.shop.guest.mapper.repo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.shop.guest.entity.User;
import org.shop.guest.mapper.UserMapper;
import org.springframework.stereotype.Component;

/**
 * 抽取DAO层
 */
@Component
@RequiredArgsConstructor
public class GuestRepo {

    private final UserMapper userMapper;

    public User findByAccount(String account) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, account));
    }

    public void updateByAccount(String account) {
        userMapper.update(null, Wrappers.<User>lambdaUpdate().eq(User::getAccount, account));
    }
}
