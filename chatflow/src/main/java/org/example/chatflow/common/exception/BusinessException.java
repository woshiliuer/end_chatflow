package org.example.chatflow.common.exception;

import lombok.Data;
import org.example.chatflow.common.enums.ErrorCode;

/**
 * @author by zzr
 */
@Data
public class BusinessException extends RuntimeException {

    private String code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(String message) {
        super(message);
    }

}
