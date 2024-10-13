package org.shop.guest.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户功能完全DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFuncAllDTO {
    /**
     * 状态 (0正常, 1封禁)
     */
    private Integer status;

    /**
     * 收款码pic
     */
    private String billcard;

    /**
     * 积分数量
     */
    private Long credit;

    /**
     * 红牌警告数量
     */
    private Integer redhit;

    /**
     * 嘉奖数量
     */
    private Integer godhit;

    /**
     * 已售出数量
     */
    private Integer solds;

    /**
     * 已获得数量
     */
    private Integer gains;

    /**
     * 粉丝数量
     */
    private Integer fans;

    /**
     * 关注人数量
     */
    private Integer followee;

    /**
     * 收藏集合, 存放商品id, ','分割
     */
    private String collections;

}
