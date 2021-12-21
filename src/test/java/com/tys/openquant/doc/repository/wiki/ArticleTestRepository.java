package com.tys.openquant.doc.repository.wiki;

import com.tys.openquant.domain.wiki.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleTestRepository extends JpaRepository<Article, Long> {
    // test code 용
    Article findTopByDelNyOrderByIdAsc(Boolean delNy);
}
