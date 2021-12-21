package com.tys.openquant.doc.repository.wiki;

import com.tys.openquant.domain.wiki.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryTestRepository extends JpaRepository<Category, Long> {
    // TestCode용
    Category findTopByOrderByIdDesc();
    List<Category> findCategoriesByIdxNotOrderByIdxAsc(Long idx);
}
