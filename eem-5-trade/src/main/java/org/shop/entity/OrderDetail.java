package org.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 交易详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_detail")
public class OrderDetail {

    public static final Integer OUT = 0;
    public static final Integer IN = 1;
    /**
     * 主键 Order唯一
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 开启交易时间
     */
    private LocalDateTime openTime;
    /**
     * 结束交易时间
     */
    private LocalDateTime checkoutTime;
    /**
     * 交易类型 (0 平台外, 1 本平台(未实现))
     */
    private Integer type;
    /**
     * 交易金额 (本平台才会生效)
     */
    private Integer amount;

}
