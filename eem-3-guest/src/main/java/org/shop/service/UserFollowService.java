package org.shop.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.entity.UserFollow;
import org.shop.entity.vo.UserVO;

import java.util.List;

public interface UserFollowService extends IService<UserFollow> {
    // 查阅主表 UserService

    /**
     * 关注/取消关注
     */
    void follow(Long followUserId, Boolean isFollow);

    /**
     * 是否关注
     */
    boolean isFollow(Long followUserId);

    /**
     * 共同关注
     */
    List<UserVO> shareFollow(Long id);
}
