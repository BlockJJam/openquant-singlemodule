package com.tys.openquant.wiki.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WikiTestDto {

    public static class ArticleUpdateRequest{
        private Object id;
        @JsonProperty("category_id")
        private Object categoryId;
        @JsonProperty("is_public")
        private Object publiced;
        private Object title;
        private Object contents;
        private Object overview;

        public ArticleUpdateRequest(Object id, Object categoryId, Object publiced, Object title, Object contents, Object overview) {
            this.id = id;
            this.categoryId = categoryId;
            this.publiced = publiced;
            this.title = title;
            this.contents = contents;
            this.overview = overview;
        }

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Object categoryId) {
            this.categoryId = categoryId;
        }

        public Object getPubliced() {
            return publiced;
        }

        public void setPubliced(Object publiced) {
            this.publiced = publiced;
        }

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public Object getContents() {
            return contents;
        }

        public void setContents(Object contents) {
            this.contents = contents;
        }

        public Object getOverview() {
            return overview;
        }

        public void setOverview(Object overview) {
            this.overview = overview;
        }
    }
}
