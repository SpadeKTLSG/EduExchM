package org.shop.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单详情完全DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailAllDTO {

    /**
     * 开启交易时间
     */
    private LocalDateTime openTime;

    /**
     * 结束交易时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 交易类型 (0 平台外, 1 本平台(未实现))
     */
    private Integer type;

    /**
     * 交易金额 (本平台才会生效)
     */
    private Integer amount;
}
