package org.shop.admin.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工登录DTO
 * 登录时使用, 包含账号和密码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoginDTO {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机(验证码)
     */
    private String phone;


    /**
     * 验证码
     */
    private String code;
}
