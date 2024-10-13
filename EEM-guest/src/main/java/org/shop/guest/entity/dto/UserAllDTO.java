package org.shop.guest.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户完全DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAllDTO {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机
     */
    private String phone;
}
