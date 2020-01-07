package com.github.linkeer8802.octopus.core.serializer;

import java.nio.charset.Charset;

/**
 * 对象序列化器
 * @author wrd
 * @param <T> 序列化的类型
 */
public interface Serializer<T> {

    Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    /**
     * 序列化对象
     * @param t 待序列化的对象
     * @return 字节数组
     * @throws SerializationException 序列化错误时抛出的异常
     */
    byte[] serialize(T t) throws SerializationException;

    /**
     * 反序列化对象
     * @param bytes 字节数组
     * @return 反序列化后的对象
     * @throws SerializationException 反序列化错误时抛出的异常
     */
    T deserialize(byte[] bytes) throws SerializationException;
}
