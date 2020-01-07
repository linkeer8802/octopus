package com.github.linkeer8802.octopus.core;

import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.core.exception.ConflictingAggregateVersionException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 聚合根工厂接口的抽象实现
 * @author weird
 * @see AggregateRootFactory
 * @see AggregateRootRepository
 */
@Slf4j
public abstract class AbstractAggregateRootFactory<T extends EventSourcingAggregateRoot<T, ? extends Serializable>, M>
        implements AggregateRootFactory<T, M>, AggregateRootRepository<T> {

    private List<Object> eventSubscribers;
    /**
     * 版本冲突检测标识
     */
    private Boolean versionConflictingCheck;
    /**
     * 同步更新聚合根版本号到数据库实体对象的标识
     */
    private Boolean updateVersionToRepository;

    public AbstractAggregateRootFactory() {
        setUpdateVersionToRepository(true);
        setVersionConflictingCheck(true);

        this.eventSubscribers = new CopyOnWriteArrayList<>();
        //把自身添加到事件订阅器中
        getEventSubscribers().add(this);
    }

    /**
     * 通过数据模型对象创建{@link DomainCreatedEvent}领域事件，
     * @param model 数据模型对象
     * @return {@link DomainCreatedEvent}领域事件对象
     */
    protected abstract DomainCreatedEvent createByModel(M model);

    /**
     * 从数据库中加载数据模型对象
     * @param id 聚合根ID
     * @return 用于创建聚合根的数据模型容器对象
     */
    protected ModelWithVersion<M> loadModel(Serializable id) {
        throw new UnsupportedOperationException("请在子类中实现该方法");
    }

    /**
     * 聚合根的版本更新回调方法，聚合根发布一个事件后，会递增版本号。
     * 需在子类中覆盖此方法，以便更新聚合根对应的数据实体对象的版本号信息到数据库中。
     * @param id 聚合根ID
     * @param oldVersion 聚合根更新前的版本号
     * @param newVersion 聚合根更新后的版本号
     * @return 成功执行更新数据库记录影响的条数，等于0表示更新失败，大于0表示更新成功
     */
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {return null;}

    @Override
    @SuppressWarnings("unchecked")
    public final T create(M entity, Class<T> clz) {
        T aggregateRoot = createAggregateRoot(clz);
        aggregateRoot.register(getEventSubscribers().toArray());
        applyEvent(entity, aggregateRoot);
        return aggregateRoot;
    }

    @Override
    public final List<Object> getEventSubscribers() {
        return eventSubscribers;
    }

    void setVersionConflictingCheck(Boolean versionConflictingCheck) {
        this.versionConflictingCheck = versionConflictingCheck;
    }

    void setUpdateVersionToRepository(Boolean updateVersionToRepository) {
        this.updateVersionToRepository = updateVersionToRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <ID extends Serializable> AggregateRootContainer<T> load(ID id, Class<T> clz) {
        AggregateRootContainer<T> resultWithAggregateRoot = loadFromModel(id, clz);
        T aggregateRoot = resultWithAggregateRoot.aggregateRoot;
        aggregateRoot.register(getEventSubscribers().toArray());
        return resultWithAggregateRoot;
    }

    @SuppressWarnings("unchecked")
    private <ID extends Serializable> AggregateRootContainer<T> loadFromModel(ID id, Class<T> clz) {
        T aggregateRoot;
        ModelWithVersion<M> modelWithVersion = loadModel(id);
        aggregateRoot = createAggregateRoot(clz);
        if (modelWithVersion == null || modelWithVersion.model == null) {
            return AggregateRootContainer.empty(aggregateRoot);
        }
        Objects.requireNonNull(modelWithVersion.version, "版本号不能为null");
        applyEvent(modelWithVersion.model, aggregateRoot);
        //从Repository生成的aggregateRoot，需要重新设置aggregateRoot的版本号
        aggregateRoot.setVersion(modelWithVersion.version);
        return new AggregateRootContainer<>(aggregateRoot);
    }

    /**
     * 根据聚合根类型创建一个新的聚合根实例
     * @param clz 聚合根类型对象
     * @param <T> 聚合根类型
     * @return 新的聚合根实例
     */
    static <T extends EventSourcingAggregateRoot<T, ? extends Serializable>> T createAggregateRoot(Class<T> clz) {
        T aggregateRoot;
        try {
            Constructor<T> constructor = clz.getDeclaredConstructor();
            constructor.setAccessible(true);
            aggregateRoot = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("聚合根实例创建失败：[" + clz.getName() + "]", e);
        }
        return aggregateRoot;
    }

    @SuppressWarnings("unchecked")
    private void applyEvent(M model, T aggregateRoot) {
        DomainCreatedEvent domainCreatedEvent = createByModel(model);
        aggregateRoot.publishEvent(domainCreatedEvent);
    }
    /**
     * 如果事件源是EventSourcingAggregateRoot
     * 递增EventSourcingAggregateRoot的版本号，并通知外部AggregateRootRepository更新版本号
     */
    @OnEvent
    @SuppressWarnings("unused")
    private void onDomainEvent(DomainEvent event) {
        EventsHolder.addEvent(event);

        if (event instanceof DomainExceptionEvent) {
            return;
        }

        if (event.getTarget() instanceof EventSourcingAggregateRoot) {
            Long oldVersion = event.getAggregateRootVersion();
            //递增EventSourcingAggregateRoot实例的版本号
            Long newVersion = incrementAggregateRootVersion((EventSourcingAggregateRoot)event.getTarget());
            //更新外部AggregateRootRepository的版本号
            if (Boolean.TRUE.equals(this.updateVersionToRepository)) {
                Integer affectedRow = updateVersion(event.getAggregateRootId(), oldVersion, newVersion);
                if (Boolean.TRUE.equals(this.versionConflictingCheck) && affectedRow != null && affectedRow.equals(0)) {
                    throw new ConflictingAggregateVersionException(String.format("聚合根版本冲突，当前版本：%d", oldVersion));
                }
            }
        }
    }

    /**
     * 递增EventSourcingAggregateRoot的版本号
     * @param aggregateRoot 聚合根对象
     * @return 递增后的版本号
     */
    private Long incrementAggregateRootVersion(EventSourcingAggregateRoot aggregateRoot) {
        return aggregateRoot.incrementVersion();
    }
}
