package org.example.chatflow.handler;

import jakarta.validation.ConstraintViolationException;
import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.ErrorCode;
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
        return CurlResponse.failure(ErrorCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public CurlResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        return CurlResponse.failure(ErrorCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public CurlResponse<Void> handleBusinessException(BusinessException ex) {
        return CurlResponse.failure(ex.getCode(),ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public CurlResponse<Void> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return CurlResponse.failure(ErrorCode.INTERNAL_ERROR);
    }
}
