package org.example.chatflow.common.enums;

/**
 * @author by zzr
 */
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ApplyDirection {

    OUTGOING(1, "我发出的申请"),

    INCOMING(2, "收到的申请");

    private final Integer code;

    private final String description;

}
