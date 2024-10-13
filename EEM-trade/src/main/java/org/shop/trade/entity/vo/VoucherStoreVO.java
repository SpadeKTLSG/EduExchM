package org.shop.trade.entity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 券视图
 * <p>用于展示券的信息</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "券视图")
public class VoucherStoreVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 副名称
     */
    private String subname;

    /**
     * 类型(一般 0 / 秒杀 1)
     */
    private Integer type;

    /**
     * 功能字段(基础 0 / 高级 1 / 超级 2)
     */
    private Integer func;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 描述
     */
    private String description;
}
