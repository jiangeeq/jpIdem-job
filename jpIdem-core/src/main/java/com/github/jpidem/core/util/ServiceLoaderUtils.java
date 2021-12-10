package com.github.jpidem.core.util;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 提供可插拔式的服务—能够扫描目录或者远程类的资源，加载到内存中，并执行。
 *
 * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
 */
public class ServiceLoaderUtils {

    /**
     * 私有构造器
     */
    private ServiceLoaderUtils() {
    }

    /**
     * 根据传入的接口类,遍历META-INF/services目录下的以该类命名的文件中的所有类,并实例化返回
     *
     * @param clazz
     * @return T
     * @author 掘金-蒋老湿[773899172@qq.com] 公众号:十分钟学编程
     */
    public static <T> T loadService(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        Iterator<T> iterator = serviceLoader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new IllegalArgumentException("无法在META-INF/services找到" + clazz.getName() + "的实例");
    }
}