package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单完全DTO
 *
 * @author SK
 * @date 2024/06/05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAllDTO {

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


}
