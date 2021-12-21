package com.tys.openquant.marketdata.exception;

import net.bytebuddy.implementation.bytecode.Throw;

public class PriceException extends RuntimeException{
    public PriceException() {
        super();
    }

    public PriceException(String message){
        super(message);
    }

    public PriceException(String message, Throwable cause){
        super(message, cause);
    }

    public PriceException(Throwable cause){
        super(cause);
    }

    protected PriceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
