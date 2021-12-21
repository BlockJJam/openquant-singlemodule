package com.tys.openquant.wiki.repository;

import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.domain.wiki.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{
    Optional<Article> findArticleByIdAndIsPublic(Long id, Boolean isPublic);
    Optional<Article> findArticleByIdAndDelNy(Long id, Boolean delNy);
    List<Article> findAllByDelNyOrderByCategoryAsc(Boolean delNy);
    Optional<Article> findTopByCategoryOrderByIdxDesc(Category category);

    Long countByDelNy(Boolean delNy);
    Long countByCategory(Category category);
}
