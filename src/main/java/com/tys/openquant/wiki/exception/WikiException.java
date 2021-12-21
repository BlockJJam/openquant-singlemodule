package com.tys.openquant.wiki.exception;

public class WikiException extends RuntimeException{
    public WikiException() {
        super();
    }

    public WikiException(String message) {
        super(message);
    }

    public WikiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WikiException(Throwable cause) {
        super(cause);
    }

    protected WikiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
