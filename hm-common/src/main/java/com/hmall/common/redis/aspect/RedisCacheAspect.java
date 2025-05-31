package com.hmall.common.redis.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.redis.annotation.RedisCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAspect {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(redisCacheable)")
    public Object cache(ProceedingJoinPoint joinPoint, RedisCacheable redisCacheable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(signature.getMethod());
        Object[] args = joinPoint.getArgs();

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        Expression expression = parser.parseExpression(redisCacheable.key());
        String dynamicKey = expression.getValue(context, String.class);

        String key = redisCacheable.prefix() + ":{" + dynamicKey + "}";
        RBucket<String> bucket = redissonClient.getBucket(key);

        if (bucket.isExists()) {
            log.debug("Cache hit: {}", key);
            String json = bucket.get();
            return objectMapper.readValue(json, signature.getReturnType());
        }

        Object result = joinPoint.proceed();
        String json = objectMapper.writeValueAsString(result);
        bucket.set(json, redisCacheable.ttl(), redisCacheable.timeUnit());
        log.debug("Cache put: {}", key);
        return result;
    }
}