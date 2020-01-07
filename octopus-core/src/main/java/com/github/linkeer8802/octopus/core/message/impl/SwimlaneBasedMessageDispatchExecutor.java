package com.github.linkeer8802.octopus.core.message.impl;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基于分区的消息分发执行器
 * @author weird
 */
public class SwimlaneBasedMessageDispatchExecutor implements Executor {

    private Runnable active;
    private final Executor executor;
    private final Queue<Runnable> tasks;

    public SwimlaneBasedMessageDispatchExecutor(Executor executor) {
        this.executor = executor;
        this.tasks = new LinkedBlockingQueue<>();
    }

    @Override
    public void execute(Runnable command) {
        Objects.requireNonNull(command);
        synchronized (tasks) {
            tasks.offer(() -> {
                try {
                    command.run();
                } finally {
                    scheduleNext();
                }
            });
            if (active == null) {
                scheduleNext();
            }
        }
    }

    private void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            executor.execute(active);
        }
    }
}
