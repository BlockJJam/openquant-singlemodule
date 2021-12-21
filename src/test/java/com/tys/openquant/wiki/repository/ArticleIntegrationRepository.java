package com.tys.openquant.wiki.repository;

import com.tys.openquant.domain.wiki.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleIntegrationRepository extends JpaRepository<Article, Long> {
    Article findTopByDelNyOrderByIdAsc(Boolean delNy);
    Article findTopByDelNyAndIsPublicOrderByIdAsc(Boolean delNy, Boolean isPublic);
}
