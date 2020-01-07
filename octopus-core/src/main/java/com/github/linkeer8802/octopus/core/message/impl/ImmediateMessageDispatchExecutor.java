package com.github.linkeer8802.octopus.core.message.impl;

import java.util.concurrent.Executor;

/**
 * 直接调用的MessageDispatchExecutor
 * @author weird
 */
public class ImmediateMessageDispatchExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
