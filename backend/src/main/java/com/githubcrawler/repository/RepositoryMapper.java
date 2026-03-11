package com.githubcrawler.repository;

import com.githubcrawler.model.Repository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepositoryMapper {

    /**
     * Upsert: INSERT ... ON CONFLICT (full_name) DO UPDATE SET ...
     */
    void insert(Repository repository);

    /**
     * Paginated query with optional filters and sort.
     *
     * @param language nullable — filters WHERE language = #{language}
     * @param minStars nullable — filters WHERE stars >= #{minStars}
     * @param sort     "stars" (default) or "updated_at"
     * @param offset   0-based row offset
     * @param limit    page size
     */
    List<Repository> findAll(
            @Param("language") String language,
            @Param("minStars") Integer minStars,
            @Param("sort") String sort,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * Count rows matching the same filters (no pagination) — used for totalElements.
     */
    long countAll(
            @Param("language") String language,
            @Param("minStars") Integer minStars
    );
}
