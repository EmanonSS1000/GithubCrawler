package com.githubcrawler.service;

import com.githubcrawler.config.GithubProperties;
import com.githubcrawler.config.GithubProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenRotationService {

    private final GithubProperties githubProperties;
    private final ResourceLoader resourceLoader;

    private List<String> tokens;
    private final AtomicInteger index = new AtomicInteger(0);

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource(githubProperties.getTokens());
            if (!resource.exists()) {
                log.warn("tokens.txt not found at '{}'. Crawler will run unauthenticated (60 req/hour limit).",
                        githubProperties.getTokens());
                tokens = List.of();
                return;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                tokens = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .toList();
            }
            log.info("Loaded {} GitHub token(s) from '{}'.", tokens.size(), githubProperties.getTokens());
        } catch (Exception e) {
            log.warn("Failed to load tokens from '{}': {}. Continuing without authentication.",
                    githubProperties.getTokens(), e.getMessage());
            tokens = List.of();
        }
    }

    /** Returns the next token in round-robin order, or null if no tokens are loaded. */
    public String nextToken() {
        if (tokens.isEmpty()) return null;
        return tokens.get(Math.abs(index.getAndIncrement() % tokens.size()));
    }

    public boolean hasTokens() {
        return !tokens.isEmpty();
    }
}
