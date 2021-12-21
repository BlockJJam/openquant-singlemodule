package com.tys.openquant.wiki.service;

import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.domain.wiki.Category;
import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.exception.WikiException;
import com.tys.openquant.wiki.exception.category.CategoryRemoveException;
import com.tys.openquant.wiki.repository.ArticleRepository;
import com.tys.openquant.wiki.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class WikiService {
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    private final String GARBAGE_CATEGORY_NAME = "garbage_category";
    private final Long GARBAGE_NUMBER = -1L;

    /**
     * USER권한의 사용자가 조회가능한 메뉴트리를 구성하여 DTO에 담아 제공하는 service
     * @return WikiDto.MenuTree
     * @author Jaemin.Joo
     */
    public WikiDto.MenuTree findPublicMenuList() {
        List<Category> categories = categoryRepository.findCategoriesByIdxNotOrderByIdxAsc(GARBAGE_NUMBER);

        WikiDto.MenuTree result = new WikiDto.MenuTree();
        result.createPublicMenuTree(categories);

        return result;
    }

    /**
     * USER권한의 사용자가 조회하는 단일 article 정보를 제공하는 service
     * @param id article의 id
     * @return WikiDto.ArticleInfo
     * @author Jaemin.Joo
     */
    public WikiDto.ArticleInfo findArticleById(Long id) {
        return articleRepository.findArticleByIdAndIsPublic(id, true)
                .map(WikiDto.ArticleInfo::new)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청 매개변수로 인한 에러"));
    }

    /**
     * ADMIN권한의 사용자가 조회 가능한 전체 메뉴트리를 제공하는 service
     * @return WikiDto.MenuTree
     * @author Jaemin.Joo
     */
    public WikiDto.MenuTree findAllMenuList() {
        WikiDto.MenuTree result = new WikiDto.MenuTree();
        result.createAdminMenuTree(
            categoryRepository.findCategoriesByIdxNotOrderByIdxAsc(GARBAGE_NUMBER)
        );

        return result;
    }

    /**
     * ADMIN권한의 사용자가 조회하는 단일 article 정보를 제공하는 service
     * @param id article의 id
     * @return WikiDto.ArticleInfoDetails
     * @author Jaemin.Joo
     */
    public WikiDto.ArticleInfoDetails findAdminArticleById(Long id) {
        return articleRepository.findArticleByIdAndDelNy(id, false)
                .map(WikiDto.ArticleInfoDetails::new)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청 매개변수로 인한 에러"));
    }

    /**
     * 이름이 겹치지 않는 새로운 카테고리 정보를 DB에 삽입하고 성공여부를 제공하는 service
     * @param newCategory
     * @return WikiDto.CategoryRegisterResult
     * @author Jaemin.Joo
     */
    public WikiDto.CategoryRegisterResult createCategory(WikiDto.NewCategory newCategory) {
        categoryRepository.findByCategoryName(newCategory.getName())
                .ifPresent((category -> {
                    throw new IllegalArgumentException(category.getCategoryName()+"는 이미 존재하는 카테고리명입니다");
                }));

        newCategory.setIdx(categoryRepository.count());

        Category category = Category.builder()
                .idx(newCategory.getIdx())
                .categoryName(newCategory.getName())
                .categoryIcon(newCategory.getCategoryIcon())
                .build();

        return Optional.ofNullable( categoryRepository.save(category))
                .map((savedCategory)-> WikiDto.CategoryRegisterResult.builder()
                        .created(true)
                        .build())
                .orElseGet(()->WikiDto.CategoryRegisterResult.builder()
                        .created(false)
                        .build());
    }

    /**
     * param을 이용해 Category Entity를 찾아서 내용을 수정하고 성공 여부를 담은 Dto를 제공해주는 service
     * @param categoryRename
     * @return WikiDto.CategoryRenameResult
     * @author Jaemin.Joo
     */
    public WikiDto.CategoryRenameResult updateCategoryInfo(WikiDto.CategoryRename categoryRename) {
        Category category = categoryRepository.findById(categoryRename.getId())
                .orElseThrow(()-> new IllegalArgumentException("요청 ID에 해당하는 Category가 없습니다"));

        log.info("[category updatable id]: {}", category.getId());

        category.setCategoryName(categoryRename.getName());
        category.setCategoryIcon(categoryRename.getCategoryIcon());

        return Optional.ofNullable(categoryRepository.saveAndFlush(category))
                .map((savedCategory)-> WikiDto.CategoryRenameResult.builder()
                        .updated(true)
                        .build())
                .orElseGet(()->WikiDto.CategoryRenameResult.builder()
                        .updated(false)
                        .build());
    }

    /**
     * param을 이용해 category entity를 찾아서 삭제한 후에 삭제 여부에 대한 DTO를 제공하는 service
     * @param id
     * @return
     * @author Jaemin.Joo
     */
    @Transactional // <- Transactional이 있어야 @Query update문 가능
    public WikiDto.CategoryDeleteResult deleteCategoryDate(Long id) {


        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("요청 ID에 해당하는 Category가 없습니다"));
        
        log.info("[category's removable id]: {}", category.getId());

        if(category.getCategoryName().equals(GARBAGE_CATEGORY_NAME)) throw new CategoryRemoveException("해당 카테고리는 지울 수 없는 카테고리입니다");

        if(category.getArticles() != null && category.getArticles().size() != 0)
                throw new CategoryRemoveException("해당 카테고리에 남아있는 Article로 인해 삭제할 수 없습니다");
        
        //1) 해당 category index가 뒷 순서인 row들은 모두 (자신의 idx)-1을 해준다
        updateIndexBeforeCategoryDelete(category);
        categoryRepository.delete(category);

        return WikiDto.CategoryDeleteResult.builder().deleted(true).build();
    }

    /**
     * 삭제가 될 category의 idx 뒷 순서의 category idx를 하나씩 앞당기는 method
     * @param deleteCategory 삭제가 될 category 정보
     * @author Jaemin.Joo
     */
    private void updateIndexBeforeCategoryDelete(Category deleteCategory) {
        Long targetIdx = deleteCategory.getIdx();

        int countUpdated = categoryRepository.updateIdxBeforeDelete(targetIdx);
        log.info("[updated category count]: {}", countUpdated);
    }

    /**
     * 새로운 Article 정보를 통해 Category, Article Entity를 저장하고 저장 여부에 대한 Dto를 제공하는 service
     * @param newArticle {categoryId, publiced, title, contents, overview}
     * @return WikiDto.ArticleRegisterResult
     * @author Jaemin.Joo
     */
    public WikiDto.ArticleRegisterResult registerArticle(WikiDto.NewArticle newArticle) {
        Category category = categoryRepository.findById(newArticle.getCategoryId())
                .orElseThrow(()-> new IllegalArgumentException("요청 ID에 해당하는 Category가 없습니다"));
        long nextIdx = getArticleNextIdx(category); // article이 있으면 마지막 index +1, 없으면 0

        log.info("[new Article's category id]: {}",category.getId());

        Article article = Article.builder()
                .category(category)
                .idx(nextIdx)
                .isPublic(newArticle.getPubliced())
                .title(newArticle.getTitle())
                .contents(newArticle.getContents())
                .overview(newArticle.getOverview())
                .build();

        return Optional.ofNullable(articleRepository.saveAndFlush(article))
                .map((savedArticle) -> WikiDto.ArticleRegisterResult.builder().registered(true).build())
                .orElseGet(()-> WikiDto.ArticleRegisterResult.builder().registered(false).build());
    }

    /**
     * 업데이트 Article 정보를 통해 Category, Article Entity를 저장하고 수정 여부에 대한 Dto를 제공하는 service
     * @param articleUpdate {id, categoryId, publiced, title, contents, overview}
     * @return WikiDto.ArticleUpdateResult
     * @author Jaemin.Joo
     */
    public WikiDto.ArticleUpdateResult updateArticleInfo(WikiDto.ArticleUpdate articleUpdate) {
        Category category = categoryRepository.findById(articleUpdate.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("요청 ID에 해당하는 Category가 없습니다"));

        log.info("[Article's update-info id]: {}", category.getId());

        Article article = articleRepository.findArticleByIdAndDelNy(articleUpdate.getId(), false)
                .orElseThrow(() -> new IllegalArgumentException("요청 ID에 해당하는 Article이 없습니다"));

        setArticleField(article, articleUpdate, category);

        return Optional.ofNullable(articleRepository.saveAndFlush(article))
                .map((updatedArticle)-> WikiDto.ArticleUpdateResult.builder().updated(true).build())
                .orElseGet(()-> WikiDto.ArticleUpdateResult.builder().updated(false).build());
    }

    /**
     * Article entity 필드를 채워주는 method
     * @param article 데이터를 채울 Article Entity
     * @param articleUpdate Article Entity에 들어갈 Dto
     * @param category Article Entity에 들어갈 Category 정보
     * @author Jaemin.Joo
     */
    private void setArticleField(Article article, WikiDto.ArticleUpdate articleUpdate, Category category) {
        //@TODO article의 category를 변경시 index를 해당 카테고리의 마지막 순번으로 변경
        if(notEqualsCategoryId(article, category)){
            article.setIdx(getArticleNextIdx(category));
        }
        article.setIsPublic(articleUpdate.getPubliced());
        article.setCategory(category);
        article.setTitle(articleUpdate.getTitle());
        article.setContents(articleUpdate.getContents());
        article.setOverview(articleUpdate.getOverview());
    }

    private Long getArticleNextIdx(Category category) {
        return articleRepository.findTopByCategoryOrderByIdxDesc(category).map(findArticle -> findArticle.getIdx() + 1).orElseGet(() -> 0L);
    }

    private boolean notEqualsCategoryId(Article article, Category category) {
        return !article.getCategory().getId().equals(
                category.getId()
        );
    }

    /**
     * 삭제될 Article id를 가지고, Article Entity의 삭제 여부와 날짜를 변경하고, 삭제 처리 여부에 대한 Dto를 제공하는 service
     * @param id delete Article id
     * @param deleteTime delete time
     * @return WikiDto.ArticleDeleteResult
     * @author Jaemin.Joo
     */
    public WikiDto.ArticleDeleteResult deleteArticleInfo(Long id, LocalDateTime deleteTime) {
        Article article = articleRepository.findArticleByIdAndDelNy(id, false)
                .orElseThrow(() -> new IllegalArgumentException("요청 ID에 해당하는 Article이 없습니다"));

        Category garbageCategory = categoryRepository.findByCategoryName(GARBAGE_CATEGORY_NAME).get();
        article.setIdx(Math.multiplyExact(article.getId(), -1L));
        article.setCategory(garbageCategory);
        article.setDelNy(true);
        article.setDeletedAt(deleteTime);

        return Optional.ofNullable(articleRepository.saveAndFlush(article))
                .map((deletedArticle)-> WikiDto.ArticleDeleteResult.builder().deleted(true).build())
                .orElseGet(()-> WikiDto.ArticleDeleteResult.builder().deleted(false).build());
    }

    /**
     * Menu tree에 사용되는 Article정보를 모두 조회
     * - request parameter를 통해 update할 id 정보와 update에 필요한 재료로 Map을 만들기
     * - Map을 이용하여 Category를 먼저 수정하고 Article 정보를 최종 수정( Article이 Category에 속한 이유 )
     * - 최종적으로, 성공여부를 제공하는 service
     * @param menuTreeUpdateDto menutree info using update
     * @return WikiDto.MenuTreeUpdateResult
     * @author Jaemin.Joo
     */
    public WikiDto.MenuTreeUpdateResult updateMenuTreeOrder(List<WikiDto.CategoryMenu> menuTreeUpdateDto) {
        List<Article> articleList = articleRepository.findAllByDelNyOrderByCategoryAsc(false);

        if( !equalsCategorySize(menuTreeUpdateDto) || !equalsSubMenuSize(menuTreeUpdateDto))
            throw new WikiException("Menu Tree의 요소 개수가 맞지 않습니다");
        
        // category 분류 후에 repository에 담을 전략
        // 1. Map< categoryId, idx> 타입의 categoryIdxMap을 만든다
        // 2. Map< articleId, idx> 타입의 subMenuIdxMap을 만든다
        // 3. Map< articleId, category> 타입의 subMenuCategoryMap을 만든다
        Map<Long, Long> categoryIdxMap = new HashMap<>();
        Map<Long, Long> subMenuIdxMap = new HashMap<>();
        Map<Long, Long> subMenuCategoryMap = new HashMap<>();

        addMenuTreeMap(menuTreeUpdateDto, categoryIdxMap, subMenuIdxMap, subMenuCategoryMap);

        // 4. Category의 index를 먼저 수정 -> 후에 Article 수정 로직과 겹치지 않기 위해
        updateCategoryIdx(categoryIdxMap);

        // 5. 이어서, Article의 index와, category 참조를 수정
        updateArticleIdxAndCategoryFK(articleList, subMenuIdxMap, subMenuCategoryMap);

        return WikiDto.MenuTreeUpdateResult.builder()
                        .updated(true)
                        .build();
    }

    /**
     * SubMenu에 속하는 Article Entity를 수정하는 method
     * @param sortedArticleEntityList
     * @param subMenuIdxMap
     * @param subMenuCategoryMap
     * @author Jaemin.Joo
     */
    private void updateArticleIdxAndCategoryFK(List<Article> sortedArticleEntityList, Map<Long, Long> subMenuIdxMap, Map<Long, Long> subMenuCategoryMap) {
        for( Article article: sortedArticleEntityList){
            article.setIdx( subMenuIdxMap.get(article.getId()) );

            Long inputCategoryId = subMenuCategoryMap.get(article.getId());
            if(isNotSameCategory(article, inputCategoryId))
                article.setCategory( categoryRepository.findById(inputCategoryId).get() );
        }
        if(!sortedArticleEntityList.isEmpty())
            articleRepository.saveAllAndFlush(sortedArticleEntityList);
    }

    private boolean isNotSameCategory(Article article, Long inputCategoryId) {
        return !article.getCategory().getId().equals(inputCategoryId);
    }

    /**
     * CategoryMenu에 속하는 Category Entity를 수정하는 method
     * @param categoryIdxMap
     * @author Jaemin.Joo
     */
    private void updateCategoryIdx(Map<Long, Long> categoryIdxMap) {
        List<Category> findCategoryList = categoryRepository.findAllByIdxNot(-1L);
        for( Category category : findCategoryList){
            category.setIdx( categoryIdxMap.get( category.getId() ) );
        }
        if(!findCategoryList.isEmpty())
            categoryRepository.saveAllAndFlush(findCategoryList);
    }

    /**
     * menutree 수정에 들어온 request parameter를 update에 필요한 Map에 담기위한 method
     * @param menuTreeUpdateDto
     * @param categoryIdxMap
     * @param subMenuIdxMap
     * @param subMenuCategoryMap
     * @author Jaemin.Joo
     */
    private void addMenuTreeMap(List<WikiDto.CategoryMenu> menuTreeUpdateDto, Map<Long, Long> categoryIdxMap, Map<Long, Long> subMenuIdxMap, Map<Long, Long> subMenuCategoryMap) {
        for(WikiDto.CategoryMenu categoryMenu : menuTreeUpdateDto){
            categoryIdxMap.put(categoryMenu.getId(), categoryMenu.getIdx());

            for(WikiDto.SubMenu subMenu: categoryMenu.getSubMenuList()){
                subMenuIdxMap.put(subMenu.getId(), subMenu.getIdx());

                subMenuCategoryMap.put(subMenu.getId(), categoryMenu.getId());
            }
        }
    }

    /**
     * request의 submenu 개수와 article 총 개수를 비교
     * @param menuTreeUpdate
     * @return boolean
     * @author Jaemin.Joo
     */
    private boolean equalsSubMenuSize(List<WikiDto.CategoryMenu> menuTreeUpdate) {
        long reqSubMenuCount = menuTreeUpdate.stream()
                .map(category -> category.getSubMenuList().stream().count())
                .collect(toList()).stream().reduce((prevCount, nextCount)-> prevCount + nextCount).get();

        return reqSubMenuCount == articleRepository.countByDelNy(false)? true: false;
    }

    /**
     * request의 category menu 개수와 Category entity의 총 개수를 비교
     * @param menuTreeUpdate
     * @return boolean
     * @author Jaemin.Joo
     */
    private boolean equalsCategorySize(List<WikiDto.CategoryMenu> menuTreeUpdate) {
        return Long.valueOf(categoryRepository.countByIdxNot(-1L)).intValue() == menuTreeUpdate.size()? true: false;
    }
}
