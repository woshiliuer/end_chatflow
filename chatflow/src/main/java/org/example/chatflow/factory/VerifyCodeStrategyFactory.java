package org.example.chatflow.factory;

import lombok.extern.slf4j.Slf4j;
import org.example.chatflow.common.enums.ErrorCode;
import org.example.chatflow.common.enums.VerfCodeType;
import org.example.chatflow.common.exception.BusinessException;
import org.example.chatflow.strategy.VerifyCodeStrategy;
import org.example.chatflow.utils.VerifyUtil;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 负责根据业务类型分发到对应的验证码发送策略。
 */
@Slf4j
@Component
public class VerifyCodeStrategyFactory {

    private final Map<VerfCodeType, VerifyCodeStrategy> strategyMap;

    public VerifyCodeStrategyFactory(List<VerifyCodeStrategy> strategies) {
        Map<VerfCodeType, VerifyCodeStrategy> map = new EnumMap<>(VerfCodeType.class);
        for (VerifyCodeStrategy strategy : strategies) {
            VerfCodeType type = strategy.supportType();
            if (map.put(type, strategy) != null) {
                log.warn("检测到重复的验证码策略，类型为 {}", type);
            }
        }
        this.strategyMap = Map.copyOf(map);
    }

    public VerifyCodeStrategy getStrategy(Integer typeCode) {
        VerfCodeType type = resolveType(typeCode);
        VerifyCodeStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        return strategy;
    }

    private VerfCodeType resolveType(Integer typeCode) {
        VerifyUtil.isTrue(typeCode == null, ErrorCode.VALIDATION_ERROR);
        try {
            return VerfCodeType.fromCode(typeCode);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }
    }
}
