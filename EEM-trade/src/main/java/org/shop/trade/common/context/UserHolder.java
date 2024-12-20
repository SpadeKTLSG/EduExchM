package org.shop.trade.common.context;


import org.shop.trade.entity.remote.UserLocalDTO;

/**
 * 用户上下文
 */
public class UserHolder {

    /**
     * 用户ThreadLocal
     */
    private static final ThreadLocal<UserLocalDTO> userTL = new ThreadLocal<>();

    /**
     * 保存用户
     */
    public static void saveUser(UserLocalDTO userLocalDTO) {
        userTL.set(userLocalDTO);
    }

    /**
     * 获取用户
     */
    public static UserLocalDTO getUser() {
        return userTL.get();
    }

    /**
     * 移除用户
     */
    public static void removeUser() {
        userTL.remove();
    }
}
