package org.example.chatflow.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.chatflow.aop.DisableAutoFill;
import org.example.chatflow.handler.MyMetaObjectHandler;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AutoFillAspect {

    @Around("@annotation(disableAutoFill)")
    public Object around(ProceedingJoinPoint joinPoint, DisableAutoFill disableAutoFill) throws Throwable {
        try {
            MyMetaObjectHandler.disableFill();
            return joinPoint.proceed();
        } finally {
            MyMetaObjectHandler.enableFill();
            MyMetaObjectHandler.clearFillFlag();
        }
    }
}