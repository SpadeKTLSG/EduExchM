package org.shop.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工VO
 * 员工登陆后使用, 不包含密码
 *
 * @author SK
 * @date 2024/05/31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "员工VO")
public class EmployeeVO {


    /**
     * 账号
     */
    private String account;


    /**
     * 姓名
     */
    private String name;


}
