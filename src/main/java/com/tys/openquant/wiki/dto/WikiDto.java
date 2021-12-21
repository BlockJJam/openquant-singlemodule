package com.tys.openquant.wiki.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tys.openquant.domain.wiki.Article;
import com.tys.openquant.domain.wiki.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public class WikiDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuTree{
        private List<CategoryMenu> data;

        /**
         * category와 submenu가 담긴 리스트가 넘어왔을 때, 사용자(public user)가 확인할 수 있는 Wiki 메뉴리스트를 담는 DTO
         * @param categories db domain으로 담겨있는 menu-tree 객체
         */
        public void createPublicMenuTree(List<Category> categories){
            this.data = categories.stream()
                    .map(createPublicCategoryMenuFunc)
                    .collect(toList());
        }
        // 밑의 Function 객체는 menu-tree의 public 버전에 맞게 CategoryMenu Stream에서 사용하기 위해 만들었다
        @JsonIgnore
        public Function<Category,CategoryMenu> createPublicCategoryMenuFunc = (Category c) ->{
            CategoryMenu categoryMenu = new CategoryMenu();
            categoryMenu.setPublicCategoryMenu(c);
            return categoryMenu;
        };

        public void createAdminMenuTree(List<Category> categories){
            this.data = categories.stream()
                    .map(craeateAdminCategoryMenuFunc)
                    .collect(toList());
        }
        @JsonIgnore
        Function<Category, CategoryMenu> craeateAdminCategoryMenuFunc = (Category c) ->{
            CategoryMenu categoryMenu = new CategoryMenu();
            categoryMenu.setAdminCategoryMenu(c);
            return categoryMenu;
        };

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryMenu{
        @NotNull
        private Long id;

        @JsonProperty("index")
        @NotNull
        @DecimalMin("0")
        private Long idx;
        @JsonProperty("name")
        private String categoryName;
        @JsonProperty("category_icon")
        private String categoryIcon;
        @JsonProperty("submenu_list")
        @Valid
        private List<SubMenu> subMenuList;

        public void setPublicCategoryMenu(Category category){
            this.id = category.getId();
            this.idx = category.getIdx();
            this.categoryName = category.getCategoryName();
            this.categoryIcon = category.getCategoryIcon();
            this.subMenuList = category.getArticles()
                    .stream()
                    .filter(article-> article.getIsPublic().equals(true))
                    .filter(article->article.getDelNy().equals(false))
                    .sorted(Comparator.comparing(article-> article.getIdx()))
                    .map(SubMenu::new)
                    .collect(toList());
        }

        public void setAdminCategoryMenu(Category category){
            this.id = category.getId();
            this.idx = category.getIdx();
            this.categoryName = category.getCategoryName();
            this.categoryIcon = category.getCategoryIcon();
            this.subMenuList = category.getArticles()
                    .stream()
                    .filter(article->article.getDelNy().equals(false))
                    .sorted(Comparator.comparing(article-> article.getIdx()))
                    .map(SubMenu::new)
                    .collect(toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubMenu{
        @NotNull
        private Long id;

        @JsonProperty("index")
        @NotNull
        @DecimalMin("0")
        private Long idx;

        @JsonProperty("article_name")
        private String articleName;
        @JsonProperty("is_public")
        private Boolean publiced;

        public SubMenu(Article article){
            this.id = article.getId();
            this.idx = article.getIdx();
            this.articleName = article.getTitle();
            this.publiced = article.getIsPublic();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleInfo{
        private Long id;

        @JsonProperty("category_id")
        private Long categoryId;
        private String title;
        private String contents;
        private String overview;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public ArticleInfo(Article article) {
            this.id = article.getId();
            this.categoryId = article.getCategory().getId();
            this.title = article.getTitle();
            this.contents = article.getContents();
            this.overview = article.getOverview();
            this.updatedAt = article.getModifiedAt();
            this.createdAt = article.getCreatedAt();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleInfoDetails{
        private Long id;
        @JsonProperty("category_id")
        private Long categoryId;
        @JsonProperty("is_public")
        private Boolean publiced;
        private String title;
        private String contents;
        private String overview;
        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;
        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public ArticleInfoDetails(Article article) {
            this.id = article.getId();
            this.categoryId = article.getCategory().getId();
            this.publiced = article.getIsPublic();
            this.title = article.getTitle();
            this.contents = article.getContents();
            this.overview = article.getOverview();
            this.updatedAt = article.getModifiedAt();
            this.createdAt = article.getCreatedAt();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewCategory{

        @NotEmpty
        @Size(min=1, max=200)
        private String name;

        @JsonProperty("category_icon")
        @NotEmpty
        @Size(min=1, max=500)
        private String categoryIcon;

        @JsonIgnore
        private Long idx = -1L;

        public void setIdx(Long idx){
            this.idx = idx;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRegisterResult{
        @JsonProperty("is_created")
        private Boolean created;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRename{
        @Min(1L)
        @NotNull
        private Long id;

        @NotEmpty
        @Size(min = 1, max= 200)
        private String name;

        @JsonProperty("category_icon")
        @NotEmpty
        @Size(min = 1, max= 500)
        private String categoryIcon;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryRenameResult{
        @JsonProperty("is_updated")
        private Boolean updated;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDeleteResult{
        @JsonProperty("is_deleted")
        private Boolean deleted;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewArticle{
        @JsonProperty("category_id")
        @NotNull
        private Long categoryId;

        @JsonProperty("is_public")
        @NotNull
        private Boolean publiced;

        @Size(min=1, max= 1000)
        @NotBlank
        private String title;
        @Size(min=1, max= 50000)
        @NotBlank
        private String contents;
        @Size(max=75000)
        @NotBlank
        private String overview;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleRegisterResult{
        @JsonProperty("is_registered")
        private Boolean registered;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleUpdate{
        @NotNull
        @DecimalMin(value="1")
        private Long id;

        @JsonProperty("category_id")
        @NotNull
        @DecimalMin(value= "1")
        private Long categoryId;

        @JsonProperty("is_public")
        @NotNull
        private Boolean publiced;

        @Size(min = 1, max= 1000)
        @NotBlank
        private String title;

        @Size(min = 1, max = 50000)
        @NotBlank
        private String contents;

        @Size(min = 1, max = 75000)
        @NotBlank
        private String overview;
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleUpdateResult{
        @JsonProperty("is_updated")
        private Boolean updated;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleDeleteResult{
        @JsonProperty("is_deleted")
        private Boolean deleted;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuTreeUpdateResult{
        @JsonProperty("is_updated")
        private Boolean updated;
    }
}
