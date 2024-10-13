package org.shop.supply.entity.remote;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocalDTO {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 账号
     */
    private String account;

}
