package wiki.xsx.core.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import wiki.xsx.core.support.ArrayType;
import wiki.xsx.core.support.MethodInfo;
import wiki.xsx.core.support.MethodParser;

import java.lang.reflect.Method;
import java.util.*;

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
     * 代表本地方法，不进行代码定位
     */
    private static final int LINE_NUMBER = -2;

    /**
     * 打印参数日志
     * @param joinPoint 切入点
     */
    @Before("@annotation(ParamLog)")
    public void beforPrint(JoinPoint joinPoint) {
        if (this.isEnable()) {
            MethodSignature signature  = (MethodSignature) joinPoint.getSignature();
            ParamLog annotation = signature.getMethod().getAnnotation(ParamLog.class);
            this.beforPrint(signature, joinPoint.getArgs(), annotation.value(), annotation.level(), annotation.position());
        }
    }

    /**
     * 打印返回值日志
     * @param joinPoint 切入点
     * @param result 返回结果
     */
    @AfterReturning(value = "@annotation(ResultLog)", returning = "result")
    public void afterPrint(JoinPoint joinPoint, Object result) {
        if (this.isEnable()) {
            MethodSignature signature  = (MethodSignature) joinPoint.getSignature();
            ResultLog annotation = signature.getMethod().getAnnotation(ResultLog.class);
            this.afterPrint(signature, result, annotation.value(), annotation.level(), annotation.position());
        }
    }

    /**
     * 打印异常日志
     * @param joinPoint 切入点
     * @param throwable 异常
     */
    @AfterThrowing(value = "@annotation(ThrowingLog)||@annotation(Log)", throwing = "throwable")
    public void throwingPrint(JoinPoint joinPoint, Throwable throwable) {
        if (this.isEnable()) {
            MethodSignature signature  = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();
            try {
                ThrowingLog annotation = method.getAnnotation(ThrowingLog.class);
                if (annotation!=null) {
                    log.error(
                            this.getThrowingInfo(
                                    annotation.value(),
                                    MethodParser.getMethodInfo(signature, -2)
                            ),
                            throwable
                    );
                }else {
                    log.error(
                            this.getThrowingInfo(
                                    method.getAnnotation(Log.class).value(),
                                    MethodParser.getMethodInfo(signature, -2)
                            ),
                            throwable
                    );
                }
            } catch (Exception e) {
                log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
            }
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
        MethodSignature signature  = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed(args);
        if (this.isEnable()) {
            Log annotation = signature.getMethod().getAnnotation(Log.class);
            this.beforPrint(signature, args, annotation.value(), annotation.level(), annotation.position());
            this.afterPrint(signature, result, annotation.value(), annotation.level(), annotation.position());
        }
        return result;
    }

    /**
     * 打印参数日志
     * @param signature 方法签名
     * @param args 参数列表
     * @param busName 业务名称
     * @param level 日志级别
     * @param position 代码定位开启标志
     */
    private void beforPrint(MethodSignature signature, Object[] args, String busName, Level level, Position position) {
        Method method = signature.getMethod();
        String methodName = method.getName();
        try {
            if (log.isDebugEnabled()) {
                if (position==Position.DEFAULT||position==Position.ENABLED) {
                    this.print(
                            level,
                            this.getBeforeInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature.getDeclaringTypeName(), methodName, signature.getParameterNames()),
                                    args
                            )
                    );
                }else {
                    this.print(
                            level,
                            this.getBeforeInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature, -2),
                                    args
                            )
                    );
                }
            }else {
                if (position==Position.ENABLED) {
                    this.print(
                            level,
                            this.getBeforeInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature.getDeclaringTypeName(), methodName, signature.getParameterNames()),
                                    args
                            )
                    );
                }else {
                    this.print(
                            level,
                            this.getBeforeInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature, -2),
                                    args
                            )
                    );
                }
            }
        } catch (Exception e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
        }
    }

    /**
     * 打印返回值日志
     * @param signature 方法签名
     * @param result 返回结果
     * @param busName 业务名称
     * @param level 日志级别
     * @param position 代码定位开启标志
     */
    private void afterPrint(MethodSignature signature, Object result, String busName, Level level, Position position) {
        Method method = signature.getMethod();
        String methodName = method.getName();
        try {
            if (log.isDebugEnabled()) {
                if (position==Position.DEFAULT||position==Position.ENABLED) {
                    this.print(
                            level,
                            this.getAfterInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature.getDeclaringTypeName(), methodName, signature.getParameterNames()),
                                    result
                            )
                    );
                }else {
                    this.print(
                            level,
                            this.getAfterInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature, -2),
                                    result
                            )
                    );
                }
            }else {
                if (position==Position.ENABLED) {
                    this.print(
                            level,
                            this.getAfterInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature.getDeclaringTypeName(), methodName, signature.getParameterNames()),
                                    result
                            )
                    );
                }else {
                    this.print(
                            level,
                            this.getAfterInfo(
                                    busName,
                                    MethodParser.getMethodInfo(signature, -2),
                                    result
                            )
                    );
                }
            }
        } catch (Exception e) {
            log.error("{}.{}方法错误", signature.getDeclaringTypeName(), methodName);
        }
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param methodInfo 方法信息
     * @param params 参数值
     * @return 返回日志信息字符串
     */
    private String getBeforeInfo(String busName, MethodInfo methodInfo, Object[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【");
        if (methodInfo.getLineNumber()==LINE_NUMBER) {
            builder.append(methodInfo.getClassAllName()).append(".").append(methodInfo.getMethodName());
        }else {
            builder.append(this.createMethodStack(methodInfo));
        }
        builder.append("】，").append("业务名称：【").append(busName).append("】，").append("接收参数：【");
        List<String> paramNames = methodInfo.getParamNames();
        int count = paramNames.size();
        if (count>0) {
            Map<String, Object> paramMap = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                paramMap.put(paramNames.get(i), this.getParam(params[i]));
            }
            return builder.append(paramMap).append("】").toString();
        }
        return builder.append("{}】").toString();
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param methodInfo 方法信息
     * @param result 返回结果
     * @return 返回日志信息字符串
     */
    private String getAfterInfo(String busName, MethodInfo methodInfo, Object result) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【");
        if (methodInfo.getLineNumber()==LINE_NUMBER) {
            builder.append(methodInfo.getClassAllName()).append(".").append(methodInfo.getMethodName());
        }else {
            builder.append("调用方法：【").append(this.createMethodStack(methodInfo));
        }
        builder.append("】，").append("业务名称：【").append(busName).append("】，").append("返回结果：【").append(result).append("】");
        return builder.toString();
    }

    /**
     * 获取日志信息字符串
     * @param busName 业务名
     * @param methodInfo 方法信息
     * @return 返回日志信息字符串
     */
    private String getThrowingInfo(String busName, MethodInfo methodInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append("调用方法：【");
        if (methodInfo.getLineNumber()==LINE_NUMBER) {
            builder.append(methodInfo.getClassAllName()).append(".").append(methodInfo.getMethodName());
        }else {
            builder.append("调用方法：【").append(this.createMethodStack(methodInfo));
        }
        builder.append("】，").append("业务名称：【").append(busName).append("】，").append("异常信息：");
        return builder.toString();
    }

    /**
     * 获取对应参数
     * @param param 参数
     * @return 返回参数
     */
    private Object getParam(Object param) {
        Class<?> type = param.getClass();
        return type.isArray() ? this.getList(type, param): param;
    }

    /**
     * 获取数组类型参数列表
     * @param valueType 数组类型
     * @param value 参数值
     * @return 返回参数列表
     */
    private List<Object> getList(Class valueType, Object value) {
        if (valueType.isAssignableFrom(ArrayType.OBJECT_ARRAY.getType())) {
            Object[] array = (Object[]) value;
            List<Object> list = new ArrayList<>(array.length);
            Collections.addAll(list, array);
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.INT_ARRAY.getType())) {
            int[] array = (int[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (int v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.LONG_ARRAY.getType())) {
            long[] array = (long[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (long v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.DOUBLE_ARRAY.getType())) {
            double[] array = (double[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (double v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.FLOAT_ARRAY.getType())) {
            float[] array = (float[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (float v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.CHAR_ARRAY.getType())) {
            char[] array = (char[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (char v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.BOOLEAN_ARRAY.getType())) {
            boolean[] array = (boolean[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (boolean v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.BYTE_ARRAY.getType())) {
            byte[] array = (byte[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (byte v : array) {
                list.add(v);
            }
            return list;
        }else if (valueType.isAssignableFrom(ArrayType.SHORT_ARRAY.getType())) {
            short[] array = (short[]) value;
            List<Object> list = new ArrayList<>(array.length);
            for (short v : array) {
                list.add(v);
            }
            return list;
        }else {
            return null;
        }
    }

    /**
     * 创建方法栈
     * @param methodInfo 方法信息
     * @return 返回栈信息
     */
    private StackTraceElement createMethodStack(MethodInfo methodInfo) {
        return new StackTraceElement(
                methodInfo.getClassAllName(),
                methodInfo.getMethodName(),
                String.format("%s.java", methodInfo.getClassSimpleName()),
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

    /**
     * 判断是否开启打印
     * @return 返回布尔值
     */
    private boolean isEnable() {
        return log.isDebugEnabled()||
                log.isInfoEnabled()||
                log.isWarnEnabled()||
                log.isErrorEnabled()||
                log.isTraceEnabled();
    }
}
