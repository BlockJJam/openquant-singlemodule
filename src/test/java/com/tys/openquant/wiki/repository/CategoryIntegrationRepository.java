package com.tys.openquant.wiki.repository;

import com.tys.openquant.domain.wiki.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryIntegrationRepository extends JpaRepository<Category, Long> {
    Category findTopByOrderByIdDesc();
    Category findTopByArticles_EmptyAndCategoryNameNotIn(List<String> categoryName);
}
