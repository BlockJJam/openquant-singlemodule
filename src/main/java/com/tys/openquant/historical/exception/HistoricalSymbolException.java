package com.tys.openquant.historical.exception;

public class HistoricalSymbolException extends RuntimeException {
    public HistoricalSymbolException() {
        super();
    }

    public HistoricalSymbolException(String message) {
        super(message);
    }

    public HistoricalSymbolException(String message, Throwable cause) {
        super(message, cause);
    }

    public HistoricalSymbolException(Throwable cause) {
        super(cause);
    }

    protected HistoricalSymbolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
