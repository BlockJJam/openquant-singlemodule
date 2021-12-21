package com.tys.openquant.wiki.repository;

import com.tys.openquant.domain.wiki.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findCategoriesByIdxNotOrderByIdxAsc(Long idx);
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findAllByIdxNot(long idx);

    Long countByIdxNot(Long idx);

    // @Query, JPQL을 이용해 update문을 작성할 시, @Modifying이 필요
    @Modifying
    @Query("update Category c set c.idx = c.idx-1 where c.idx > :idx")
    Integer updateIdxBeforeDelete(@Param("idx") Long idx);
}
