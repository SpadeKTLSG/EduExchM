package org.shop.trade.common.utils;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.common.constant.RedisConstant;
import org.shop.trade.entity.res.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Function;


/**
 * 缓存客户端工具类
 * <p>仅为示例, 在使用联合主键情况下并不适用</p>
 *
 * @author admin
 */
@Slf4j
@Component
public class CacheClient {

    /**
     * 缓存重建线程池
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            1L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024)) {

        @Override
        protected void afterExecute(Runnable runnable, Throwable throwable) {
            //execute运行 适配
            if (throwable != null) {
                log.error(throwable.getMessage(), throwable);
            }
            //submit运行 适配
            if (throwable == null && runnable instanceof Future<?>) {
                try {
                    Future<?> future = (Future<?>) runnable;
                    if (future.isDone()) {
                        future.get();
                    }
                } catch (CancellationException ce) {
                    throwable = ce;
                    log.error(ce.getMessage(), ce);
                } catch (ExecutionException ee) {
                    throwable = ee.getCause();
                    log.error(ee.getMessage(), ee);
                } catch (InterruptedException ie) {
                    log.error(ie.getMessage(), ie);
                    Thread.currentThread().interrupt();
                }
            }
        }
    };

    /**
     * stringRedisTemplate
     */
    private StringRedisTemplate stringRedisTemplate;

    /**
     * set
     *
     * @param key      key
     * @param value    value
     * @param time     time
     * @param timeUnit timeUnit
     */
//任意Java对象序列化为JSON，并存储到String类型的Key中，并可以设置TTL过期时间
    public void set(String key, Object value, Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }

    /**
     * setWithLogicExpire
     *
     * @param key      key
     * @param value    value
     * @param time     time
     * @param timeUnit timeUnit
     */
//将任意Java对象序列化为JSON，并存储在String类型的Key中，并可以设置逻辑过期时间，用于处理缓存击穿问题
    public void setWithLogicExpire(String key, Object value, Long time, TimeUnit timeUnit) {

        RedisData<Object> redisData = new RedisData<>();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }


    /**
     * queryWithPassThrough
     *
     * @param keyPrefix  keyPrefix
     * @param id         id
     * @param type       type
     * @param dbFallback dbFallback
     * @param time       time
     * @param timeUnit   timeUnit
     * @param <R>        R
     * @param <ID>       ID
     * @return R
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit) {

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);


        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        if (json != null) {
            return null;
        }

        R r = dbFallback.apply(id);


        if (r == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        String jsonStr = JSONUtil.toJsonStr(r);

        this.set(key, jsonStr, time, timeUnit);

        return r;
    }


    /**
     * queryWithLogicalExpire
     *
     * @param keyPrefix  keyPrefix
     * @param id         id
     * @param type       type
     * @param dbFallback dbFallback
     * @param time       time
     * @param timeUnit   timeUnit
     * @param <R>        R
     * @param <ID>       ID
     * @return R
     */
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit) {

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(json)) {
            return null;
        }

        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();

        //判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            return r;
        }

        //过期，尝试获取互斥锁
        String lockKey = RedisConstant.LOCK_EG_KEY + id;
        boolean flag = tryLock(lockKey);

        //获取到了锁
        if (flag) {
            //开启独立线程 实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    R tmp = dbFallback.apply(id);
                    this.setWithLogicExpire(key, tmp, time, timeUnit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(lockKey);
                }
            });

            return r;
        }

        return r;
    }

    /**
     * queryWithMutex
     *
     * @param keyPrefix  keyPrefix
     * @param id         id
     * @param type       type
     * @param dbFallback dbFallback
     * @param time       time
     * @param timeUnit   timeUnit
     * @param <R>        R
     * @param <ID>       ID
     * @return R
     */
    public <R, ID> R queryWithMutex(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit timeUnit) {

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        if (json != null) {
            return null;
        }

        String lockKey = RedisConstant.LOCK_EG_KEY + id;
        R r;
        try {

            boolean flag = tryLock(lockKey);
            if (!flag) {
                Thread.sleep(50);
                return queryWithMutex(keyPrefix, id, type, dbFallback, time, timeUnit);
            }
            r = dbFallback.apply(id);

            if (r == null) {
                stringRedisTemplate.opsForValue().set(key, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }

            this.set(key, r, time, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }
        return r;
    }

    /**
     * tryLock
     *
     * @param key key
     * @return data
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * unlock
     *
     * @param key key
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
