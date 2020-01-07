package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReturnedRecordModel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.money.MonetaryAmount;
import java.util.List;

@Getter
@ToString(callSuper = true)
public class BookReturnRecordCreatedEvent extends DomainEvent {

    private List<ReturnedRecordModel> returnedRecords;
    /**
     * 罚金
     */
    private MonetaryAmount fine;

    public BookReturnRecordCreatedEvent(List<ReturnedRecordModel> returnedRecords, MonetaryAmount fine) {
        this.fine = fine;
        this.returnedRecords = returnedRecords;
    }
}
