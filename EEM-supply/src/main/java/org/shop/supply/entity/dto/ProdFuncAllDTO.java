package org.shop.supply.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * 商品功能完全DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdFuncAllDTO {

    /**
     * 浏览量
     */
    private Long visit;


    /**
     * 状态 (0正常 / 1审核中 / 2冻结)
     */
    private Integer status;


    /**
     * 搜索权重 (默认500)
     */
    private Long weight;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 展现权重 (0 一般, 仅搜索 / 1 **首页提升榜单 /**  2 **首页提升榜单 +**  首页轮播图)
     */
    private Integer showoffStatus;

    /**
     * 展现结束时间
     */
    private LocalDateTime showoffEndtime;
}
