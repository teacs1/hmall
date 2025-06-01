package com.hmall.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedisProperties {
    private Cluster cluster;
    private String password;

    @Data
    public static class Cluster {
        private List<String> nodes;
    }
}
