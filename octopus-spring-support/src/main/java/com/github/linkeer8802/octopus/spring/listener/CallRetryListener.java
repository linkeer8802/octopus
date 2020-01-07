package com.github.linkeer8802.octopus.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CallRetryListener implements RetryListener {
    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        log.info("尝试重试");
        return true;
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (isExhausted(context)) {
            log.error("重试次数耗尽", throwable);
        }
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.error(String.format("重试失败%d次", context.getRetryCount()), throwable);
    }

    private String getState(RetryContext context) {
        return (String) context.getAttribute(RetryContext.STATE_KEY);
    }

    private String getName(RetryContext context) {
        return (String) context.getAttribute(RetryContext.NAME);
    }

    private boolean isExhausted(RetryContext context) {
        return context.hasAttribute(RetryContext.EXHAUSTED);
    }
}
