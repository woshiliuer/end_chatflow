package org.example.chatflow.handler;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.example.chatflow.common.CurlResponse;
import org.example.chatflow.common.constants.ErrorCode;
import org.example.chatflow.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public CurlResponse<Void> handleBindExceptions(Exception ex) {
        FieldError fieldError = null;
        if (ex instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
        } else if (ex instanceof BindException bindException) {
            fieldError = bindException.getBindingResult().getFieldError();
        }
        String message = fieldError != null
            ? fieldError.getDefaultMessage()
            : ErrorCode.VALIDATION_ERROR.getDefaultMessage();
        return CurlResponse.failure(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CurlResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
            .findFirst()
            .map(ConstraintViolation::getMessage)
            .orElse(ErrorCode.VALIDATION_ERROR.getDefaultMessage());
        return CurlResponse.failure(ErrorCode.VALIDATION_ERROR, message);
    }

    @ExceptionHandler(BusinessException.class)
    public CurlResponse<Void> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String message = ex.getMessage() != null ? ex.getMessage() : errorCode.getDefaultMessage();
        return CurlResponse.failure(errorCode, message);
    }

    @ExceptionHandler(Exception.class)
    public CurlResponse<Void> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return CurlResponse.failure(ErrorCode.INTERNAL_ERROR);
    }
}
