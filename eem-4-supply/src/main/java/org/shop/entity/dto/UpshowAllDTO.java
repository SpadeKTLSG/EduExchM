package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提升 DTO
 *
 * @author SK
 * @date 2024/05/31
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
