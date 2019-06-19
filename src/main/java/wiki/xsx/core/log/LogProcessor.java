package wiki.xsx.core.log;

import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import wiki.xsx.core.support.MethodInfo;
import wiki.xsx.core.support.MethodParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志处理器
 * @author xsx
 * @date 2019/6/17
 * @since 1.8
 */
@Aspect
@Slf4j
public class LogProcessor {

    /**
     * 打印参数日志
     * @param joinPoint 切入点
     */
    @Before("@annotation(ParamLog)")
    public void beforPrint(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        try {
            CtMethod method = MethodParser.getMethod(signature.getDeclaringTypeName(), methodName);
            ParamLog paramLogAnnotation;
            try {
                paramLogAnnotation = (ParamLog) method.getAnnotation(ParamLog.class);
            } catch (ClassNotFoundException e) {
                paramLogAnnotation = null;
            }
            if (paramLogAnnotation!=null) {
                this.print(
                        paramLogAnnotation.level(),
                        this.getBeforeInfo(
                                paramLogAnnotation.value(),
                                signature,
                                MethodParser.getMethodInfo(method),
                                args
                        )
                );
            }
        } catch (Exception e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
        }
    }

    /**
     * 打印返回值日志
     * @param joinPoint 切入点
     * @param result 返回结果
     */
    @AfterReturning(value = "@annotation(ResultLog)", returning = "result")
    public void afterPrint(JoinPoint joinPoint, Object result) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        try {
            CtMethod method = MethodParser.getMethod(signature.getDeclaringTypeName(), methodName);
            ResultLog resultLogAnnotation;
            try {
                resultLogAnnotation = (ResultLog) method.getAnnotation(ResultLog.class);
            } catch (ClassNotFoundException e) {
                resultLogAnnotation = null;
            }
            if (resultLogAnnotation!=null) {
                this.print(
                        resultLogAnnotation.level(),
                        this.getAfterInfo(
                                resultLogAnnotation.value(),
                                signature,
                                MethodParser.getMethodInfo(method),
                                result
                        )
                );
            }
        } catch (Exception e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
        }
    }

    /**
     * 打印异常日志
     * @param joinPoint 切入点
     * @param throwable 异常
     */
    @AfterThrowing(value = "@annotation(ThrowingLog)||@annotation(Log)", throwing = "throwable")
    public void throwingPrint(JoinPoint joinPoint, Throwable throwable) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        try {
            CtMethod method = MethodParser.getMethod(signature.getDeclaringTypeName(), methodName);
            ThrowingLog throwingAnnotation;
            try {
                throwingAnnotation = (ThrowingLog) method.getAnnotation(ThrowingLog.class);
            } catch (ClassNotFoundException e) {
                throwingAnnotation = null;
            }
            if (throwingAnnotation!=null) {
                log.error(
                        this.getThrowingInfo(
                                throwingAnnotation.value(),
                                signature,
                                MethodParser.getMethodInfo(method)
                        ),
                        throwable
                );
            }else {
                Log logAnnotation;
                try {
                    logAnnotation = (Log) method.getAnnotation(Log.class);
                } catch (ClassNotFoundException e) {
                    logAnnotation = null;
                }
                if (logAnnotation!=null) {
                    log.error(
                            this.getThrowingInfo(
                                    logAnnotation.value(),
                                    signature,
                                    MethodParser.getMethodInfo(method)
                            ),
                            throwable
                    );
                }
            }
        } catch (Exception e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
        }
    }

    /**
     * 打印环绕日志
     * @param joinPoint 切入点
     * @return 返回方法返回值
     * @throws Throwable 异常
     */
    @Around(value = "@annotation(Log)")
    public Object aroundPrint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        try {
            CtMethod method = MethodParser.getMethod(signature.getDeclaringTypeName(), methodName);
            Log logAnnotation;
            try {
                logAnnotation = (Log) method.getAnnotation(Log.class);
            } catch (ClassNotFoundException e) {
                logAnnotation = null;
            }
            if (logAnnotation!=null) {
                this.print(
                        logAnnotation.level(),
                        this.getBeforeInfo(
                                logAnnotation.value(),
                                signature,
                                MethodParser.getMethodInfo(method),
                                args
                        )
                );
            }
            Object result = joinPoint.proceed(args);
            if (logAnnotation!=null) {
                this.print(
                        logAnnotation.level(),
                        this.getAfterInfo(
                                logAnnotation.value(),
                                signature,
                                MethodParser.getMethodInfo(method),
                                result
                        )
                );
            }
            return result;
        } catch (Throwable e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
            throw e;
        }
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param signature 签名信息
     * @param methodInfo 方法信息
     * @param params 参数值
     * @return 返回日志信息字符串
     */
    private String getBeforeInfo(String busName, Signature signature, MethodInfo methodInfo, Object[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【").append(this.createMethodStack(signature, methodInfo)).append("】，")
                .append("业务名称：【").append(busName).append("】，").append("接收参数：【");
        List<String> paramNames = methodInfo.getParamNames();
        int count = paramNames.size();
        if (count>0) {
            Map<String, Object> paramMap = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                paramMap.put(paramNames.get(i), params[i]);
            }
            return builder.append(paramMap).append("】").toString();
        }
        return builder.append("{}】").toString();
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param signature 签名信息
     * @param methodInfo 方法信息
     * @param result 返回结果
     * @return 返回日志信息字符串
     */
    private String getAfterInfo(String busName, Signature signature, MethodInfo methodInfo, Object result) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【").append(this.createMethodStack(signature, methodInfo)).append("】，")
                .append("业务名称：【").append(busName).append("】，").append("返回结果：【").append(result).append("】");
        return builder.toString();
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param signature 签名信息
     * @param methodInfo 方法信息
     * @return 返回日志信息字符串
     */
    private String getThrowingInfo(String busName, Signature signature, MethodInfo methodInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【").append(this.createMethodStack(signature, methodInfo)).append("】，")
                .append("业务名称：【").append(busName).append("】，").append("异常信息：");
        return builder.toString();
    }

    /**
     * 创建方法栈
     * @param signature 签名信息
     * @param methodInfo 方法信息
     * @return 返回栈信息
     */
    private StackTraceElement createMethodStack(Signature signature, MethodInfo methodInfo) {
        return new StackTraceElement(
                signature.getDeclaringTypeName(),
                methodInfo.getMethodName(),
                String.format("%s.java", signature.getDeclaringType().getSimpleName()),
                methodInfo.getLineNumber()
        );
    }

    /**
     * 打印信息
     * @param level 日志级别
     * @param msg 输出信息
     */
    private void print(Level level, String msg) {
        switch (level) {
            case DEBUG: {
                log.debug(msg);
                break;
            }
            case INFO: {
                log.info(msg);
                break;
            }
            case WARN: {
                log.warn(msg);
                break;
            }
            case ERROR: {
                log.error(msg);
                break;
            }
            default:
        }
    }
}
