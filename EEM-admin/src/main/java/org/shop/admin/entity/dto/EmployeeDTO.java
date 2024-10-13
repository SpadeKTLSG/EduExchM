package org.shop.admin.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 员工DTO
 * 员工登陆后使用, 不包含密码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    /**
     * 账号
     */
    private String account;


    /**
     * 姓名
     */
    private String name;


}
