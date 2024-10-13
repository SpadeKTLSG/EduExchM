package org.shop.trade.common.constant;

/**
 * Redis常量
 */
public interface RedisConstant {


    //* Admin


    /**
     * 验证码Key前缀
     */
    String LOGIN_CODE_KEY_ADMIN = "admin:login:code:";


    /**
     * 验证码过期时间
     */
    Long LOGIN_CODE_TTL_ADMIN = 1800L; // 30分钟


    /**
     * 登录接受Key前缀
     */
    String LOGIN_USER_KEY_ADMIN = "admin:login:token:";


    /**
     * 登录用户过期时间
     */
    Long LOGIN_USER_TTL_ADMIN = 36000L; // 10小时


    //* Guest

    /**
     * 验证码Key前缀
     */
    String LOGIN_CODE_KEY_GUEST = "guest:login:code:";


    /**
     * 验证码过期时间
     */
    Long LOGIN_CODE_TTL_GUEST = 1800L; // 30分钟


    /**
     * 登录TOKEN Key前缀
     * <p>单机状态下默认清理掉所有的登陆TOKEN</p>
     */
    String LOGIN_USER_KEY_GUEST = "guest:login:token:";


    /**
     * 登录用户过期时间
     */
    Long LOGIN_USER_TTL_GUEST = 36000L; // 10小时


    /**
     * 用户签到Key前缀
     */
    String USER_SIGN_KEY = "sign:";


    /**
     * 用户VO浏览Key前缀
     */
    String USER_VO_KEY = "vo:";


    /**
     * 用户收藏Key前缀
     */
    String PROD_COLLECT_KEY = "guest:prod:collect:";


    //* ZEN - 秒杀逻辑 -

    /**
     * 商品秒杀逻辑库存Key前缀
     */
    String SECKILL_STOCK_KEY = "seckill:stock:";


    /**
     * 商品秒杀逻辑订单Key前缀
     */
    String SECKILL_ORDER_KEY = "seckill:order:";


    /**
     * 缓存空数据时间
     */
    Long CACHE_NULL_TTL = 2L; // 2 分钟

    /**
     * 对象锁示例
     */
    String LOCK_EG_KEY = "lock:eg:";


    //* Ultra - 限流逻辑 -


    /**
     * 发送验证码时间Key前缀
     */
    String SENDCODE_SENDTIME_KEY = "sms:sendtime:";


    /**
     * 一级限流Key前缀
     */
    String ONE_LEVERLIMIT_KEY = "limit:onelevel:";


    /**
     * 二级限流Key前缀
     */
    String TWO_LEVERLIMIT_KEY = "limit:twolevel:";


    //* Ultra - 限流逻辑 -


    /**
     * 缓存商品Key前缀
     */
    String CACHE_PROD_KEY = "cache:prod:";


    /**
     * 缓存商品过期时间
     */
    Long CACHE_PROD_TTL = 30L; // 30 分钟


    /**
     * 商品锁Key前缀
     */
    String LOCK_PROD_KEY = "lock:prod:";


    /**
     * 商品锁过期时间
     */
    Long LOCK_PROD_TTL = 10L; // 10 秒


    /**
     * 商品锁失败等待时间
     */
    Long LOCK_PROD_FAIL_WT = 50L; // 50 秒


    /**
     * 活动商品过期时间
     */
    Long ACTIVE_PROD_TTL = 30L; // 30 秒


}
