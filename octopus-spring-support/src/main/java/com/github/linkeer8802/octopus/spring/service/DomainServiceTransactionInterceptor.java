package com.github.linkeer8802.octopus.spring.service;

import com.github.linkeer8802.octopus.spring.listener.CallbackContext;
import com.github.linkeer8802.octopus.spring.listener.DomainServiceTransactionListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域服务类事务方法拦截器，可监听领域服务方法的事务执行的完整生命周期,
 * 在spring容器中注册实现{@link DomainServiceTransactionListener}接口的bean即可。
 * @author wrd
 * @see DomainServiceTransactionListener
 */
@Aspect
@Slf4j
public class DomainServiceTransactionInterceptor implements Ordered {

    private static final String KEY_INITIALIZED = "DOMAIN_SERVICE_TRANSACTION_INITIALIZED";

    private List<DomainServiceTransactionListener> listeners;

    public DomainServiceTransactionInterceptor(List<DomainServiceTransactionListener> listeners) {
        this.listeners = new ArrayList<>(0);
        if (listeners != null && !listeners.isEmpty()) {
            this.listeners.addAll(listeners);
        }
    }

    @Pointcut("@within(com.github.linkeer8802.octopus.spring.annotation.DomainService)")
    private void domainService() {}

    @Around("domainService()")
    public Object aroundDomainService(ProceedingJoinPoint pjp) throws Throwable {
        CallbackContext context = getCallbackContext(pjp);
        listeners.forEach(listener -> listener.onBefore(context));
        //存在非只读事务时才拦截
        if (TransactionSynchronizationManager.isSynchronizationActive()
                && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {

            //嵌套事务时不需重新注册
            boolean initialized = TransactionSynchronizationManager.hasResource(KEY_INITIALIZED);

            if (!initialized) {
                try {
                    context.setTransactionActive(true);
                    listeners.forEach(listener -> listener.onActive(context));
                    TransactionSynchronizationManager.bindResource(KEY_INITIALIZED, true);
                } finally {
                    registerTransactionSynchronization(context);
                }
            }
        }
        Object result;
        try {
            result = pjp.proceed(pjp.getArgs());
            context.setResult(result);
        } catch (Throwable t) {
            context.setThrowable(t);
            listeners.forEach(listener -> listener.onError(context));
            throw t;
        } finally {
            listeners.forEach(listener -> listener.onAfter(context));
        }
        return result;
    }

    private CallbackContext getCallbackContext(ProceedingJoinPoint pjp) {
        CallbackContext context = new CallbackContext();
        context.setArgs(pjp.getArgs());
        context.setTarget(pjp.getTarget());
        context.setSignature(pjp.getSignature());
        context.setTransactionActive(false);
        return context;
    }

    private void registerTransactionSynchronization(CallbackContext context) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){

            @Override
            public void beforeCommit(boolean readOnly) {
                listeners.forEach(listener -> listener.onBeforeCommit(context));
            }

            @Override
            public void beforeCompletion() {
                listeners.forEach(listener -> listener.onBeforeCompletion(context));
            }

            @Override
            public void afterCommit() {
                listeners.forEach(listener -> listener.onAfterCommit(context));
            }

            @Override
            public void afterCompletion(int status) {
                context.setTransactionStatus(status);
                listeners.forEach(listener -> listener.onAfterCompletion(context));
                TransactionSynchronizationManager.unbindResourceIfPossible(KEY_INITIALIZED);
            }
        });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
