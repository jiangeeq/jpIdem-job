package com.github.jpidem.core.util;

import com.github.jpidem.core.IllegalRetryException;
import com.github.jpidem.core.RetryFunction;
import com.github.jpidem.core.RetryHandler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 重试执行器的相关工具类
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class RetryHandlerUtils {
    /**
     * 私有构造器
     */
    private RetryHandlerUtils() {
    }

    /**
     * 检查被@RetryFunction标注的方法参数是否合法
     * <p>
     * Method的输入参数最多只能有一个，且不能是Object或者其他不能序列化和反序列化等类型
     *
     * @param method java.lang.reflect.Method
     * @return
     */
    public static boolean isRetryFunctionMethod(Method method) {
        // 方法上有@RetryFunction且方法上只有一个参数对象
        if (method.getAnnotation(RetryFunction.class) != null && method.getParameterCount() == 1) {
            // 参数不能是Object.class
            return !Object.class.equals(method.getParameterTypes()[0]);
        }
        return false;
    }

    /**
     * 检查重试执行器的处理参数是否合法
     *
     * @param clazz
     * @return void
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static void validateRetryHandler(Class<?> clazz) {
        Type interfaceType = getRetryHandlerGenericInterface(clazz);
        Class<?> argsInputType = Object.class;
        if (interfaceType instanceof ParameterizedType) {
            // 获取参数化类型的实际类型的数组，这里取第一个；例如：Food<Fruit,Vegetable>那么将返回 [Fruit,Vegetable]
            Type type = ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
            // 参数类型是对象类型
            if (type instanceof Class) {
                // 返回参数化类型的实际类型的数组 例如：Food<Vegetable>那么将返回 [Vegetable]这1个类型的数组
                argsInputType = (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
            } else {
                throw new IllegalRetryException("重试方法的参数类型不能是集合类等带泛型的");
            }
        }
        if (Object.class.equals(argsInputType)) {
            throw new IllegalRetryException("重试方法的参数类型[" + argsInputType + "]不能是Object或其他不能序列化和反序列化的类型");
        } else if (Collection.class.isAssignableFrom(argsInputType) || Map.class.isAssignableFrom(argsInputType)) {
            throw new IllegalRetryException("重试方法的参数类型[" + argsInputType + "]不能是集合类等带泛型的");
        }
    }

    /**
     * 检查重试方法中的参数类型是否合法
     *
     * @param method to @see
     * @return void
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     * @see Method
     */
    public static void validateRetryFunction(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalRetryException(method.toString() + ": 重试方法有且只能有一个参数");
        }
        Class<?> clazz = method.getParameterTypes()[0];
        if (Object.class.equals(clazz)) {
            throw new IllegalRetryException(method.toString() + ": 重试方法的参数类型不能是Object或其他不能序列化和反序列化的类型");
        } else if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
            throw new IllegalRetryException(method.toString() + ": 重试方法的参数类型不能是集合类等带泛型的");
        }
    }

    /**
     * 判断RetryHandler实现类中 handle()方法参数是否合法
     *
     * @param targetClass 目标class
     * @param method      目标class中的method
     * @return boolean
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static boolean isRetryHandlerMethod(Class<?> targetClass, Method method) {
        // 方法名是handle且参数只有一个且是桥接方法是合法结构
        if ("handle".equals(method.getName()) && method.getParameterCount() == 1 && method.isBridge() && method.isSynthetic()) {
            // RetryHandler接口有泛型，所以使用该方式特殊处理
            return true;
        }
        // 获取是RetryHandler类型的Type
        Type interfaceType = getRetryHandlerGenericInterface(targetClass);
        if (interfaceType == null) {
            return false;
        }
        Class<?> argsInputType = Object.class;
        if (interfaceType instanceof ParameterizedType) {
            argsInputType = (Class<?>) ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
        }
        // 方法名是handle且参数只有一个且方法参数类型和接口Type类型一直
        return "handle".equals(method.getName()) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(argsInputType);
    }

    /**
     * 获取是RetryHandler类型的Type
     *
     * @param clazz 对象的class
     * @return Type 接口的Type
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static Type getRetryHandlerGenericInterface(Class<?> clazz) {
        // 返回表示某些接口的Type，该Type由此对象所表示的类或接口。
        Type[] types = clazz.getGenericInterfaces();
        Optional<Type> interfaceTypeOptional = Stream.of(types).filter(i -> i.getTypeName().startsWith(RetryHandler.class.getName())).findAny();
        if (interfaceTypeOptional.isPresent()) {
            return interfaceTypeOptional.get();
        }
        // clazz不是接口实现，则判断是不是有父类继承
        Class<?> superclass = clazz.getSuperclass();
        return Objects.nonNull(superclass) ? superclass : null;
    }

    /**
     * 拼接方法类名+方法名组成字符串
     *
     * @param method
     * @return String 用作任务的唯一标识identity
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static String getMethodIdentity(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }
}
