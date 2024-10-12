package org.shop.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工完全DTO
 *
 * @author SK
 * @date 2024/06/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAllDTO {

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;


}
