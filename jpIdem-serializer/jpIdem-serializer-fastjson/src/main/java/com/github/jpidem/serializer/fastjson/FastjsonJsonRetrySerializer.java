package com.github.jpidem.serializer.fastjson;

import com.alibaba.fastjson.JSON;
import com.github.jpidem.core.RetrySerializer;
import org.kohsuke.MetaInfServices;

/**
 * @author yuni[mn960mn@163.com]
 */
@MetaInfServices(RetrySerializer.class)
public class FastjsonJsonRetrySerializer implements RetrySerializer {

    @Override
    public String serialize(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public Object deserialize(String value, Class<?> clazz) {
        return JSON.parseObject(value, clazz);
    }
}