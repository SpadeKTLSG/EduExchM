package org.shop.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品定位DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdLocateDTO {

    /**
     * 名称
     */
    private String name;

    /**
     * 对应用户的ID
     */
    private Long userId;

}
