package org.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 交易核心
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("`order`")
public class Order {

    public static final Integer OPEN = 0;
    public static final Integer WAITCHECK = 1;
    public static final Integer TALKING = 2;
    public static final Integer EXCHANGING = 3;
    public static final Integer OVER = 4;
    public static final Integer STOP = 5;
    /**
     * 主键 Order唯一
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 买方ID
     */
    private Long buyerId;
    /**
     * 卖方ID
     */
    private Long sellerId;
    /**
     * 商品ID
     */
    private Long prodId;
    /**
     * 交易状态
     * 0 买家开启, 1 等待卖家确认, 2 交涉中, 3 正在交易, 4 交易完成, 5 终止
     */
    private Integer status;

}
