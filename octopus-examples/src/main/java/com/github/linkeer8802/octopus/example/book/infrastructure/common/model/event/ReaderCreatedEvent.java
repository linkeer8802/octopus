package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReaderModel;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ReaderCreatedEvent extends DomainCreatedEvent<String> {

    private ReaderModel dataModel;

    public ReaderCreatedEvent(ReaderModel dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public String getId() {
        return dataModel.getId();
    }
}
