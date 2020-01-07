package com.github.linkeer8802.octopus.core;

/**
 * 用于创建聚合根的数据模型容器对象
 * @author weird
 */
public class ModelWithVersion<M> {
    public final M model;
    public final Long version;

    /**
     * 数据模型容器构造方法
     * @param model 数据模型对象
     * @param version 聚合根版本号
     */
    public ModelWithVersion(M model, Long version) {
        this.model = model;
        this.version = version;
    }

    /**
     * 创建一个空的ModelWithVersion对象
     * @param <U>
     * @return
     */
    public static <U> ModelWithVersion<U> empty() {
        return new ModelWithVersion<>(null, null);
    }
}
