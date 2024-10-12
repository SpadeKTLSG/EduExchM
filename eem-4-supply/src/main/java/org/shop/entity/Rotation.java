package org.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 轮播
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rotation")
public class Rotation {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


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
