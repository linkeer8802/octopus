package com.github.linkeer8802.octopus.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.linkeer8802.octopus.core.exception.DomainRuntimeException;

/**
 * 领域外部异常事件，该事件不应该被领域对象捕获
 * @author wrd
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DomainExceptionEvent extends DomainEvent {
    private DomainRuntimeException exception;

    private DomainExceptionEvent() {}

    public DomainExceptionEvent(DomainRuntimeException exception) {
        this.exception = exception;
    }

    public DomainRuntimeException getException() {
        return exception;
    }
}
