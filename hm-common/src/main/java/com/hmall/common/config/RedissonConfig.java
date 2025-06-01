package com.hmall.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.redis.util.RedisKeyUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfig {

    private final RedisProperties redisProperties;

    public RedissonConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties properties) {
        if (redisProperties.getCluster() == null || redisProperties.getCluster().getNodes() == null) {
            throw new IllegalArgumentException("Redis 配置未加载，请检查 spring.redis.cluster.nodes");
        }
        Config config = new Config();
        String[] nodes = properties.getCluster().getNodes().stream()
                .map(ip -> "redis://" + ip)
                .toArray(String[]::new);

        config.useClusterServers()
                .addNodeAddress(nodes)
                .setPassword(properties.getPassword());

        return Redisson.create(config);
    }

    @Bean
    public RedisKeyUtil redisKeyUtil(RedissonClient client, ObjectMapper mapper) {
        return new RedisKeyUtil(client, mapper);
    }
}
