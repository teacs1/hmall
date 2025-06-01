package com.hmall.common.redis.constant;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum RedisKeyEnum {

    // ===== 用户模块 =====
    /** 用户基础信息 */
    USER_INFO("user", "user:{%s}:info", 1800),

    /** 用户登录 Token */
    USER_TOKEN("user", "user:{%s}:token", 1800),

    /** 登录失败计数（防止暴力破解） */
    USER_LOGIN_FAIL_COUNT("user", "user:{%s}:login:fail_count", 600),

    // ===== 商品模块 =====
    /** 商品库存 */
    ITEM_STOCK("item", "item:%s:stock", 0), // 永不过期

    /** 商品详情缓存 */
    ITEM_DETAIL("item", "item:%s:detail", 1800),

    // ===== 购物车模块 =====
    /** 用户购物车商品列表 */
    CART_ITEMS("cart", "cart:{%s}:items", 0),

    /** 用户购物车操作锁 */
    CART_LOCK("cart", "lock:cart:{%s}:checkout", 10),

    // ===== 订单模块 =====
    /** 订单状态缓存 */
    ORDER_STATUS("order", "order:{%s}:%s:status", 3600),

    /** 创建订单的锁 */
    ORDER_CREATE_LOCK("order", "lock:order:{%s}:create", 5),

    // ===== 支付模块 =====
    /** 支付状态缓存 */
    PAY_STATUS("pay", "pay:{%s}:%s:status", 600),

    // ===== 公共限流器/验证码模块 =====
    /** 短信验证码 */
    SMS_CODE("common", "sms:%s:code", 300),

    /** 接口限流 key */
    RATE_LIMIT("common", "rate_limit:{%s}:%s", 1);

    // ============================
    private final String module;     // 所属模块
    private final String keyPattern; // Key 模板
    private final int ttlSeconds;    // 过期时间（秒），0 表示不过期

    RedisKeyEnum(String module, String keyPattern, int ttlSeconds) {
        this.module = module;
        this.keyPattern = keyPattern;
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * 获取格式化后的 key（String.format 实现）
     * @param args 动态填充参数
     * @return 实际 Redis key
     */
    public String format(Object... args) {
        return String.format(this.keyPattern, args);
    }

    /**
     * 获取 TTL（以 TimeUnit 为单位）
     */
    public long getTtl(TimeUnit unit) {
        return unit.convert(ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 是否有过期时间
     */
    public boolean hasExpire() {
        return ttlSeconds > 0;
    }
}
