package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品分类 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdCateAllDTO {

    /**
     * 分类名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

}
