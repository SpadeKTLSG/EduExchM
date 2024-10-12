package org.shop.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券定位DTO
 *
 * @author SK
 * @date 2024/06/03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherLocateDTO {

    /**
     * 名称
     */
    private String name;


}
