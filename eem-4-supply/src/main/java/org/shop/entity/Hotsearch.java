package org.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 热搜
 *
 * @author SK
 * @date 2024/05/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("hotsearch")
public class Hotsearch {

    /**
     * 主键 Hotsearch唯一
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
     * 对应商品浏览量
     */
    private Long visit;

}
