package com.githubcrawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "github")
public class GithubProperties {

    /** Spring Resource path, e.g. "file:./tokens.txt" */
    private String tokens;

    private Api api = new Api();

    @Data
    public static class Api {
        private String baseUrl;
        private int perPage = 100;
    }
}
