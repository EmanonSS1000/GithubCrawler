package com.githubcrawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {

    private String queue;
    private String lockPrefix;
    private long lockExpireMs;
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class RateLimit {
        private int maxRequestsPerHour;
        private String bucketKey;
    }
}
