package com.yar.microservices.uaa.controller.handler;


import com.yar.microservices.uaa.bundle.MessageCodes;
import com.yar.microservices.uaa.bundle.MessageService;
import com.yar.microservices.uaa.controller.errors.BusinessException;
import com.yar.microservices.uaa.dto.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class RestServiceExceptionHandler extends ResponseEntityExceptionHandler {


    private final MessageService messageService;

    @Autowired
    public RestServiceExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> defaultExceptionHandler(Exception ex) {
        String actionCode = MessageCodes.INTERNAL_SERVER_ERROR;
        String actionMessage = messageService.getMessage(actionCode);
        GeneralResponse generalResponse = new GeneralResponse(actionCode, actionMessage);
        log.error(actionCode + ": " + actionMessage);
        return new ResponseEntity<>(generalResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<?> businessExceptionHandler(BusinessException ex) {

        String actionCode = ex.getErrorCode();
        String actionMessage = messageService.getMessage(actionCode);
        GeneralResponse generalResponse = new GeneralResponse(actionCode, actionMessage);
        log.error(actionCode + ": " + actionMessage);
        HttpStatus httpStatus = ex.getHttpStatus();
        if (httpStatus == null) httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(generalResponse, httpStatus);
    }


}
