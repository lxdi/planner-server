package com.sogoodlabs.planner.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        log.error("Error: ", ex);
        String message = ex.getMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html;charset=UTF-8");
        return handleExceptionInternal(ex, message, headers, HttpStatus.CONFLICT, request);
    }

}
