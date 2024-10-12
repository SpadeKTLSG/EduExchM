package org.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 商品功能
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("prod_func")
public class ProdFunc {

    public static final Integer NORMAL = 0;
    public static final Integer CHECKING = 1;
    public static final Integer FROZEN = 2;
    public static final Integer BASIC = 0;
    public static final Integer SENIOR = 1;
    public static final Integer SUPER = 2;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
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
     * 展现权重 (0 一般, 仅搜索 / 1 高级:  首页提升榜单 /  2  超级: 首页提升榜单 +  首页轮播图)
     */
    private Integer showoffStatus;
    /**
     * 展现结束时间
     */
    private LocalDateTime showoffEndtime;

}
