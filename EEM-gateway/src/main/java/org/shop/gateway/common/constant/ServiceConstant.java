package org.shop.gateway.common.constant;

/**
 * 服务中使用的常量
 */
public interface ServiceConstant {

    /**
     * 展示提升持续时间 [级别 -> 持续时间]
     * <p>基础, 高级, 超级</p>
     */
    Long[] UPSHOW_LEVEL_TTL = {1L, 3L, 7L};
}
