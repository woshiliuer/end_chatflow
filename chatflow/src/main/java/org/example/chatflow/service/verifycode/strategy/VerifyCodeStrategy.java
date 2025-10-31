package org.example.chatflow.service.verifycode.strategy;

import org.example.chatflow.common.entity.CurlResponse;
import org.example.chatflow.common.enums.VerfCodeType;
import org.example.chatflow.model.dto.User.GetVerfCodeDTO;

/**
 * 定义验证码发送策略的统一接口。
 */
public interface VerifyCodeStrategy {

    /**
     * @return 当前策略支持的验证码业务类型
     */
    VerfCodeType supportType();

    /**
     * 执行验证码发送流程。
     *
     * @param dto 请求参数
     * @return 接口响应
     */
    CurlResponse<String> process(GetVerfCodeDTO dto);
}
