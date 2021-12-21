package com.tys.openquant.wiki.enums;

public enum ArticleFieldSize {
    TITLE_MAX(1000), CONTENTS_MAX(50000), OVERVIEW_MAX(75000)
    ;
    private int value;
    ArticleFieldSize(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
