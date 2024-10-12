package org.shop.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品完全VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商品全部VO")
public class ProdAllVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 价格
     */
    private Long price;

    /**
     * 图片 集合
     */
    private String images;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 描述
     */
    private String description;

    /**
     * 对应用户的ID
     */
    private Long userId;

}
