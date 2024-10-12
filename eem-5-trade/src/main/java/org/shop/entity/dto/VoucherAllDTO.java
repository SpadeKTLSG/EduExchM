package org.shop.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 优惠券完全DTO
 *
 * @author SK
 * @date 2024/06/03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherAllDTO {

    /**
     * 名称
     */
    private String name;

    /**
     * 副名称
     */
    private String subname;

    /**
     * 状态(0 未使用, 1 已使用, 2 已过期)
     */
    private Integer status;

    /**
     * 类型(一般 0 / 秒杀 1)
     */
    private Integer type;

    /**
     * 可使用对象(卖方 0 / 买方 1)
     */
    private Integer user;

    /**
     * 功能字段(基础 0 / 高级 1 / 超级 2)
     */
    private Integer func;

    /**
     * 积分价值
     */
    private Integer value;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 生效时间
     */
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    private LocalDateTime endTime;

    /**
     * 描述
     */
    private String description;


    /**
     * 对应用户id
     */
    private Long userId;

}
