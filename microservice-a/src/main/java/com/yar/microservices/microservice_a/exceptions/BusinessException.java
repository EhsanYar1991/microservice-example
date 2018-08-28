package com.yar.microservices.microservice_a.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Created by EhsanYar.
 */
public class BusinessException extends Exception implements Serializable {

    private static final long serialVersionUID = 6271934941966893011L;

    private String errorCode;
    private HttpStatus httpStatus;

    public BusinessException(String errorCode) {
        super(errorCode);
        this.errorCode = errorCode;

    }

    public BusinessException(String errorCode, HttpStatus httpStatus) {
        super(errorCode);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;

    }

    public BusinessException(String errorCode, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    protected BusinessException(String errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
