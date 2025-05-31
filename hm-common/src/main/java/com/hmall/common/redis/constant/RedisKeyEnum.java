package com.hmall.common.redis.constant;

import lombok.Getter;

@Getter
public enum RedisKeyEnum {

    // === 用户模块 ===
    USER_INFO("user:{%s}:info"),
    USER_TOKEN("user:{%s}:token"),

    // === 购物车模块 ===
    CART_ITEMS("cart:{%s}:items"),
    CART_LOCK("lock:cart:{%s}:checkout"),

    // === 订单模块 ===         用户id:订单id
    ORDER_STATUS("order:{%s}:%s:status"),
    ORDER_LOCK("lock:order:{%s}:%s"),

    // === 商品模块 ===
    ITEM_STOCK("item:%s:stock"), // 与用户无关，不用 hash tag

    // === 支付模块 ===         用户id:支付id
    PAY_STATUS("pay:{%s}:%s:status");

    private final String keyPattern;

    RedisKeyEnum(String keyPattern) {
        this.keyPattern = keyPattern;
    }

    /**
     * 根据 pattern 格式化实际 key
     */
    public String format(Object... args) {
        return String.format(this.keyPattern, args);
    }
}
