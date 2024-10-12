package org.shop.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播 DTO
 *
 * @author SK
 * @date 2024/06/08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RotationAllDTO {


    /**
     * 对应商品ID
     */
    private Long prodId;

    /**
     * 对应商品名
     */
    private String name;

    /**
     * 对应商品图片
     */
    private String picture;

    /**
     * 对应商品权重
     */
    private Long weight;

}
