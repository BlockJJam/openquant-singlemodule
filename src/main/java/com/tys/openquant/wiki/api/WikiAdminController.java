package com.tys.openquant.wiki.api;

import com.tys.openquant.wiki.dto.WikiDto;
import com.tys.openquant.wiki.service.WikiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/wiki/")
@PreAuthorize("hasAnyRole('ADMIN')")
public class WikiAdminController {
    private final WikiService wikiService;

    /**
     * 관리자에 대한 전체 메뉴 트리를 보여주는 API
     * @return WikiDto.MenuTree
     * @author Jaemin.Joo
     */
    @GetMapping("/menu-tree")
    public ResponseEntity<WikiDto.MenuTree> getAdminMenuTree(){
        return ResponseEntity.ok(wikiService.findAllMenuList());
    }

    /**
     * 메뉴트리에서 선택된 하나의 Article 정보를 조회하는 API
     * @param id menu-tree에서 선택된 submenu가 가지고 있는 article id
     * @return WikiDto.ArticleInfoDetails
     * @author Jaemin.Joo
     */
    @GetMapping("/article")
    public ResponseEntity<WikiDto.ArticleInfoDetails> getArticleById(@RequestParam @NotNull Long id){
        return ResponseEntity.ok(wikiService.findAdminArticleById(id));
    }

    /**
     * 새로운 카테고리에 대한 단일 정보를 생성하여 생성 여부를 제공하는 API
     * @param newCategory 새로운 category의 name, icon 정보
     * @return WikiDto.CategoryRegisterResult
     * @author Jaemin.Joo
     */
    @PostMapping("/category/create")
    public ResponseEntity<WikiDto.CategoryRegisterResult> registerNewCategory(@Valid @RequestBody WikiDto.NewCategory newCategory) {
        return ResponseEntity.ok(wikiService.createCategory(newCategory));
    }

    /**
     * 단일 category 정보를 업데이트하여 성공 여부를 제공하는 API
     * @param categoryRename wiki에서 업데이트할 단일 category 정보
     * @return WikiDto.CategoryRenameResult
     * @author Jaemin.Joo
     */
    @PostMapping("/category/rename")
    public ResponseEntity<WikiDto.CategoryRenameResult> renameCategory(@Valid @RequestBody WikiDto.CategoryRename categoryRename){
        return ResponseEntity.ok(wikiService.updateCategoryInfo(categoryRename));
    }

    /**
     * 단일 category 정보를 삭제하고 성공 여부를 제공하는 API
     * @param id wiki에서 삭제할 category의 id
     * @return WikiDto.CategoryDeleteResult
     * @author Jaemin.Joo
     */
    @GetMapping("/category/delete")
    public ResponseEntity<WikiDto.CategoryDeleteResult> deleteCategory(@RequestParam @NotNull @Min(1L) Long id){
        return ResponseEntity.ok(wikiService.deleteCategoryDate(id));
    }

    /**
     * Article 정보를 삽입한 뒤, 성공 여부를 제공하는 API
     * @param newArticle {categoryId, publiced, title, contents, overview}
     * @return WikiDto.ArticleRegisterResult
     * @author Jaemin.Joo
     */
    @PostMapping("/article/register")
    public ResponseEntity<WikiDto.ArticleRegisterResult> registerNewArticle(@Valid @RequestBody WikiDto.NewArticle newArticle){
        return ResponseEntity.ok(wikiService.registerArticle(newArticle));
    }

    /**
     * 기존 Article 정보를 수정한 뒤, 성공 여부를 제공하는 API
     * @param articleUpdate {id, categoryId, publiced, title, contents, overview}
     * @return WikiDto.ArticleUpdateResult
     * @author Jaemin.Joo
     */
    @PostMapping("/article/update")
    public ResponseEntity<WikiDto.ArticleUpdateResult> updateArticle(@Valid @RequestBody WikiDto.ArticleUpdate articleUpdate){
        return ResponseEntity.ok(wikiService.updateArticleInfo(articleUpdate));
    }

    /**
     * 요청으로 들어온 Article 정보를 삭제한 뒤, 성공 여부를 제공하는 API
     * @param id Article id
     * @return WikiDto.ArticleDeleteResult
     * @author Jaemin.Joo
     */
    @GetMapping("/article/delete")
    public ResponseEntity<WikiDto.ArticleDeleteResult> deleteArticle(@RequestParam @NotNull Long id){
        return ResponseEntity.ok(wikiService.deleteArticleInfo(id, LocalDateTime.now()));
    }

    /**
     * 순서가 정립된 Menu Tree 정보를 통해 MenuTree를 업데이트한 뒤, 성공 여부를 제공하는 API
     * @param menuTreeUpdate Array<Category>{ id, index, name, Array<SubMenu>{id, index, article_name } }
     * @return WikiDto.MenuTreeUpdateResult
     */
    @PostMapping("/category/update")
    public ResponseEntity<WikiDto.MenuTreeUpdateResult> updateMenuTree(@Valid @RequestBody List<WikiDto.CategoryMenu> menuTreeUpdate){
        return ResponseEntity.ok(wikiService.updateMenuTreeOrder(menuTreeUpdate));
    }
}
