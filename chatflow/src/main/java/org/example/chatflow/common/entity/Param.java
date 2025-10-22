package org.example.chatflow.common.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * @author by zzr
 */

@Schema(
        title = "参数包装类"
)
public class Param<T> {
    @Schema(
            title = "业务参数"
    )
    private @NotNull
    @Valid T param;

    public T getParam() {
        return this.param;
    }

    public void setParam(final T param) {
        this.param = param;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Param)) {
            return false;
        } else {
            Param<?> other = (Param)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$param = this.getParam();
                Object other$param = other.getParam();
                if (this$param == null) {
                    if (other$param != null) {
                        return false;
                    }
                } else if (!this$param.equals(other$param)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Param;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $param = this.getParam();
        result = result * 59 + ($param == null ? 43 : $param.hashCode());
        return result;
    }

    public String toString() {
        return "Param(param=" + this.getParam() + ")";
    }
}
