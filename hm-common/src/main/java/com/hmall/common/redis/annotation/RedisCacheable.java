package com.hmall.common.redis.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCacheable {
    String prefix(); // key 前缀，如 user、item
    String key(); // SpEL 表达式，如 "#userId"
    long ttl() default 30; // 默认 30 分钟
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
