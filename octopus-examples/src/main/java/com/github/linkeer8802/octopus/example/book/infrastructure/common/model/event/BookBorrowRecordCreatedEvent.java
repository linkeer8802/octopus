package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event;

import com.github.linkeer8802.octopus.core.DomainEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString(callSuper = true)
public class BookBorrowRecordCreatedEvent extends DomainEvent {

    private List<BorrowedRecordModel> borrowingRecords;

    public BookBorrowRecordCreatedEvent(List<BorrowedRecordModel> borrowingRecords) {
        this.borrowingRecords = borrowingRecords;
    }
}
