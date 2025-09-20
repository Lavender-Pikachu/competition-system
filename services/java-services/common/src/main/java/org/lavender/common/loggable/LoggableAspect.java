package org.lavender.common.loggable;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.lavender.common.domain.OperLoggable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;

@Aspect
@Component
public class LoggableAspect {
    public static final Logger log = LoggerFactory.getLogger(LoggableAspect.class);

    @Pointcut("@annotation(org.lavender.common.loggable.Loggable)")
    public void loggablePointCut(){}

    @Around("loggablePointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        }finally{
            long elapsed = System.currentTimeMillis() - start;
            log.info("Executed {}.{} in {} ms", joinPoint.getTarget().getClass().getSimpleName(),joinPoint.getSignature(), elapsed);
        }
    }

    /**
     * 处理完请求后执行
     * @param joinPoint  切点
     * @param jsonResult json值
     */
    @AfterReturning(pointcut = "loggablePointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult){
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(pointcut = "loggablePointCut()",throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e){
        handleLog(joinPoint, e, null);
    }


    @Async
    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult){

    }

    /**
     * 获取注解中对方法的描述信息，用于Controller层注解
     * @param loggable
     * @param operLoggable
     * @throws Exception
     */
    public void getControllerMethodDescription(Loggable loggable, OperLoggable operLoggable) throws Exception{
        operLoggable.setTitle(loggable.title());
        operLoggable.setOperatorType(loggable.operatorType().ordinal());
        if(loggable.recordRequest()){
            setRequestValue(operLoggable);
        }
    }

    /**
     * 获取请求参数，在log中存储
     * @param operLoggable
     */
    private void setRequestValue(OperLoggable operLoggable){
        try{
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes == null) return;

            HttpServletRequest request = attributes.getRequest();
            operLoggable.setUrl(request.getRequestURI());
            operLoggable.setHttpMethod(request.getMethod());
            operLoggable.setIp(request.getRemoteAddr());
            operLoggable.setParam(request.getParameterMap());
            operLoggable.setSuccess(true);
        }catch (Exception e){
            operLoggable.setSuccess(false);
            log.warn("获取请求参数失败: {}", e.getMessage());
        }
    }

    /**
     * 是否存在注解,如果存在就获取
     */
    private Loggable getAnnotationLoggable(JoinPoint joinPoint) throws Exception{
        Signature  signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if(method != null){
            return method.getAnnotation(Loggable.class);
        }
        return null;
    }
}
