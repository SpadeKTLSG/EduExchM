package org.shop.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工登录VO
 * 传递员工信息, 不包含密码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "员工登录VO")
public class EmployeeLoginVO {

    /**
     * 主键值
     */
    private Long id;


    /**
     * 账号
     */
    private String account;


    /**
     * 姓名
     */
    private String name;


}
