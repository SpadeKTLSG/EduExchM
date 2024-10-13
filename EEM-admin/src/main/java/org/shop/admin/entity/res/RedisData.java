package org.shop.admin.entity.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Redis数据封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisData<T> {

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 通用数据对象
     */
    private T data;
}
