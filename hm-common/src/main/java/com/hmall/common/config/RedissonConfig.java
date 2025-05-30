package com.hmall.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RedissonConfig {

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.cluster.nodes}")
    private List<String> clusterNodes;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String[] nodes = clusterNodes.stream()
                .map(ip -> "redis://" + ip)
                .toArray(String[]::new);

        config.useClusterServers()
                .addNodeAddress(nodes)
                .setPassword(redisPassword);

        return Redisson.create(config);
    }
}
