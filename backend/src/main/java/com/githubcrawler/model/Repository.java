package com.githubcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Repository {

    private Long id;
    private String fullName;
    private String name;
    private String description;
    private String language;
    private Integer stars;
    private Integer forks;
    private Integer openIssues;
    private String htmlUrl;
    private String[] topics;          // PostgreSQL TEXT[] — mapped via StringArrayTypeHandler
    private OffsetDateTime updatedAt;
    private OffsetDateTime crawledAt;
    private String rawData;           // PostgreSQL JSONB — mapped via JsonbTypeHandler
}
