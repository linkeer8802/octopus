package com.github.linkeer8802.octopus.spring.listener;

import com.github.linkeer8802.octopus.spring.service.DomainServiceTransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * DomainService事务方法执行监听器
 * @author weird
 * @date 2019/12/20
 * @see DomainServiceTransactionInterceptor
 */
public interface DomainServiceTransactionListener {
    /**
     * 执行事务方法前调用
     * @param context 回调上下文
     */
    default void onBefore(CallbackContext context) {}

    /**
     * 事务激活时调用，此时context的transactionActive为true
     * @param context 回调上下文
     */
    default void onActive(CallbackContext context) {}

    /**
     * 事务提交前调用
     * @param context 回调上下文
     * @see TransactionSynchronization#beforeCommit(boolean)
     */
    default void onBeforeCommit(CallbackContext context) {}

    /**
     * 事务提交/回滚前调用
     * @param context 回调上下文
     * @see TransactionSynchronization#beforeCompletion()
     */
    default void onBeforeCompletion(CallbackContext context) {}

    /**
     * 事务提交后调用
     * @param context 回调上下文
     * @see TransactionSynchronization#afterCommit()
     */
    void onAfterCommit(CallbackContext context);

    /**
     * 事务提交/回滚后调用，可从context的transactionStatus属性获取事务的状态
     * @param context 回调上下文
     * @see TransactionSynchronization#STATUS_COMMITTED
     * @see TransactionSynchronization#STATUS_ROLLED_BACK
     * @see TransactionSynchronization#STATUS_UNKNOWN
     * @see TransactionSynchronization#afterCompletion(int)
     */
    default void onAfterCompletion(CallbackContext context) {}
    /**
     * 执行领域服务方法出错后调用
     * @param context 回调上下文
     */
    default void onError(CallbackContext context) {}
    /**
     * 执行领域服务方法完成后调用，可从context获取事务方法执行结果
     * @param context 回调上下文
     */
    default void onAfter(CallbackContext context) {}
}
