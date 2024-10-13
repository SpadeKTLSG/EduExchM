package org.shop.supply.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提升 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpshowAllDTO {

    /**
     * 对应商品ID
     */
    private Long prodId;

    /**
     * 对应商品名
     */
    private String name;

}
