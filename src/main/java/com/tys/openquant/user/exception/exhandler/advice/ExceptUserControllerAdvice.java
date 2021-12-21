package com.tys.openquant.user.exception.exhandler.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tys.openquant.user.api.AuthController;
import com.tys.openquant.user.exception.UserException;
import com.tys.openquant.user.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.ConstraintViolationException;

@Slf4j
// basePackages 밑에 있는 모든 컨트롤러의 예외처리에 대한 핸들링을 하겠다는 설정
// Servlet을 거치는 2번동작이 없음
@RestControllerAdvice(basePackages = "com.tys.openquant.user.api")
public class ExceptUserControllerAdvice {

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    private ErrorResult constraintViolationException(ConstraintViolationException e){
        log.error("[validationHandler] error", e);
        return new ErrorResult("METHOD_ARGUMENT_NOT_VALIDATION", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    private com.tys.openquant.marketdata.exception.exhandler.ErrorResult httpMessageNotReadableExHandler(HttpMessageNotReadableException e){
        log.error("[validationHandler] error", e);
        return new com.tys.openquant.marketdata.exception.exhandler.ErrorResult("HTTP_MSG_NOT_READABLE_VALIDATION", e.getMessage());
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    private ErrorResult internalExHandler(Exception e){
        log.error("[exceptionHandler] error", e);
        return new ErrorResult("INTERNAL_EXCEPTION", e.getMessage());
    }

    /**
     * User Exception Handler
     */
    @ExceptionHandler
    private ResponseEntity<ErrorResult> userExHandler(UserException e){
        log.error("[userHandler] error", e);
        return new ResponseEntity<>(new ErrorResult("USER_EXCEPTION", e.getMessage()),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResult> usernameNotFoundExHandler(UsernameNotFoundException e){
        log.error("[usernameExHandler] error" , e);
        return new ResponseEntity<>(new ErrorResult("USERNAME_NOT_FOUND", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     *  Security Exception Handler
     **/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler
    private ErrorResult unAuthorized(HttpClientErrorException.Unauthorized e){
        log.error("[exceptionHandler] e", e);
        return new ErrorResult("UNAUTHORIZED", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    private ErrorResult forbidden(HttpClientErrorException.Forbidden e){
        log.error("[exceptionHandler] e", e);
        return new ErrorResult("FORBIDDEN", e.getMessage());
    }


    @ExceptionHandler
    private ResponseEntity<ErrorResult> badCredentialsException(BadCredentialsException e){
        log.error("[badCredentialsExHandler] error" , e);
        return new ResponseEntity<>(new ErrorResult("BAD_REQUEST_CREDENTIAL", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResult>  accessDeniedException(AccessDeniedException e){
        log.error("[AccessDeniedException] error", e);
        return new ResponseEntity<>(new ErrorResult("ACCESS_DENIED", e.getMessage()), HttpStatus.FORBIDDEN);
    }

}
