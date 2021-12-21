package com.tys.openquant.wiki.exception.category;

import com.tys.openquant.wiki.exception.WikiException;

public class CategoryRemoveException extends RuntimeException {
    public CategoryRemoveException() {
        super();
    }
    public CategoryRemoveException(String message) {
        super(message);
    }

    public CategoryRemoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryRemoveException(Throwable cause) {
        super(cause);
    }

    protected CategoryRemoveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
