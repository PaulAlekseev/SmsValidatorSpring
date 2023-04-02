package com.example.SmsValidator.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger globalLogger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException exception) {
        Logger logger = LoggerFactory.getLogger((Class<?>) exception.getType());
        logger.error(exception.getLocalizedMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(exception));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> handleServletException(Exception exception) {
        globalLogger.error(exception.getLocalizedMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getLocalizedMessage()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception exception) {
        globalLogger.error(exception.getLocalizedMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(exception));
    }

}