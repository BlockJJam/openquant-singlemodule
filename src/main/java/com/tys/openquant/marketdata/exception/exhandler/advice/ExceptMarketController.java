package com.tys.openquant.marketdata.exception.exhandler.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tys.openquant.marketdata.exception.PriceException;
import com.tys.openquant.marketdata.exception.SymbolException;
import com.tys.openquant.user.exception.UserException;
import com.tys.openquant.marketdata.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice(basePackages = "com.tys.openquant.marketdata.api")
public class ExceptMarketController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    private ErrorResult illegalExHandler(IllegalArgumentException e){
        log.error("[exceptionHandler] error", e);
        return new ErrorResult("BAD_REQUEST_PARAMETER", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ErrorResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
        log.error("[validHandler] error", e);
        return new ErrorResult("METHOD_ARGUMENT_NOT_VALID", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    private ErrorResult internalExHandler(Exception e){
        log.error("[exceptionHandler] error", e);
        return new ErrorResult("INTERNAL_EXCEPTION", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler
    private ErrorResult badRequest(HttpServerErrorException.BadGateway e){
        log.error("[exceptionHandler] e", e);
        return new ErrorResult("BAD_GATEWAY", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    private ErrorResult notFound(HttpClientErrorException.NotFound e){
        log.error("[exceptionHandler] e", e);
        return new ErrorResult("NOT_FOUND", e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    private ErrorResult constraintViolationException(ConstraintViolationException e){
        log.error("[validationHandler] error", e);
        return new ErrorResult("METHOD_ARGUMENT_NOT_VALIDATION", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ErrorResult httpMessageNotReadableExHandler(HttpMessageNotReadableException e){
        log.error("[validationHandler] error", e);
        return new ErrorResult("HTTP_MSG_NOT_READABLE_VALIDATION", e.getMessage());
    }
    /*************************** 여기까지는 Base Exception 처리가 필요해보임 ***************************/

    @ExceptionHandler
    private ResponseEntity<ErrorResult> priceExHandler(PriceException e){
        log.error("[priceExHandler] error", e);
        return new ResponseEntity<>(new ErrorResult("PRICE_EXCEPTION", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResult> symbolExHandler(SymbolException e){
        log.error("[symbolExHandler] error", e);
        return new ResponseEntity<>(new ErrorResult("SYMBOL_EXCEPTION", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
