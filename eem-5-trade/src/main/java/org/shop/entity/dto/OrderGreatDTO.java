package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单超级DTO (包含订单和订单详情)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderGreatDTO {

    /**
     * 买方ID
     */
    private Long buyerId;

    /**
     * 卖方ID
     */
    private Long sellerId;

    /**
     * 商品ID
     */
    private Long prodId;

    /**
     * 交易状态
     * 0 买家开启, 1 等待卖家确认, 2 交涉中, 3 正在交易, 4 交易完成, 5 终止
     */
    private Integer status;


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
