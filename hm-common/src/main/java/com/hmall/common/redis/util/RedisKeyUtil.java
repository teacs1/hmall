package com.hmall.common.redis.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.redis.constant.RedisKeyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类，基于 Redisson 和 Jackson 封装。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyUtil {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    /**
     * 设置缓存对象
     */
    public <T> void set(RedisKeyEnum keyEnum, T value, Object... args) {
        String key = keyEnum.format(args);
        RBucket<String> bucket = redissonClient.getBucket(key);
        try {
            String json = objectMapper.writeValueAsString(value);
            if (keyEnum.hasExpire()) {
                bucket.set(json, keyEnum.getTtl(TimeUnit.SECONDS), TimeUnit.SECONDS);
            } else {
                bucket.set(json);
            }
        } catch (Exception e) {
            log.error("Redis 序列化失败，key：{}", key, e);
            throw new RuntimeException("Redis 序列化失败：" + key, e);
        }
    }

    /**
     * 获取缓存对象（普通 Class 类型）
     */
    public <T> T get(RedisKeyEnum keyEnum, Class<T> clazz, Object... args) {
        String key = keyEnum.format(args);
        RBucket<String> bucket = redissonClient.getBucket(key);
        String json = bucket.get();
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Redis 反序列化失败，key：{}", key, e);
            throw new RuntimeException("Redis 反序列化失败：" + key, e);
        }
    }

    /**
     * 获取缓存对象（支持复杂泛型类型）
     */
    public <T> T get(RedisKeyEnum keyEnum, TypeReference<T> typeRef, Object... args) {
        String key = keyEnum.format(args);
        RBucket<String> bucket = redissonClient.getBucket(key);
        String json = bucket.get();
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error("Redis 反序列化失败，key：{}", key, e);
            throw new RuntimeException("Redis 反序列化失败：" + key, e);
        }
    }

    /**
     * 删除缓存
     */
    public void delete(RedisKeyEnum keyEnum, Object... args) {
        String key = keyEnum.format(args);
        redissonClient.getBucket(key).delete();
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(RedisKeyEnum keyEnum, Object... args) {
        String key = keyEnum.format(args);
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 设置过期时间
     */
    public void expire(RedisKeyEnum keyEnum, long ttl, TimeUnit unit, Object... args) {
        String key = keyEnum.format(args);
        redissonClient.getBucket(key).expire(ttl, unit);
    }
}
