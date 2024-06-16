/*
 * Created 2018-7-2 11:11:47
 */
package cn.com.yting.kxy.web.util;

import cn.com.yting.kxy.web.KxyWebException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Aspect
@Component
public class DebugOnlyAspect {

    @Value("${kxy.web.debug}")
    private boolean debugEnabled;

    @Around("@annotation(cn.com.yting.kxy.core.DebugOnly) && execution(* *(..))")
    public Object denyDebugOnlyMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (debugEnabled) {
            return joinPoint.proceed();
        } else {
            throw KxyWebException.unknown("不能被使用的测试方法");
        }
    }
}
