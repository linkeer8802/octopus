package com.github.linkeer8802.octopus.example.book.domain.reader;

import com.github.linkeer8802.octopus.core.EventSourcingAggregateRoot;
import com.github.linkeer8802.octopus.core.eventbus.OnEvent;
import com.github.linkeer8802.octopus.core.util.CollectionUtils;
import com.github.linkeer8802.octopus.core.util.Identifiers;
import com.github.linkeer8802.octopus.example.book.domain.book.Book;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.BorrowedRecordModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReturnedRecordModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookBorrowRecordCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.BookReturnRecordCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.ReaderCreatedEvent;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import lombok.Getter;
import lombok.ToString;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString(callSuper = true)
public class Reader extends EventSourcingAggregateRoot<Reader, String> {

    private String name;
    /**
     * 借出未归还的图书记录
     */
    private List<BorrowedRecord> borrowingBookRecords;
    /**
     * 未交罚金
     */
    private MonetaryAmount unpaidFine;

    private Reader() {}

    /**
     * 借书
     * @param books 待借阅图书
     * @param borrowDate 借出日期
     */
    public void borrowBooks(List<Book> books, LocalDate borrowDate) {
        List<BorrowedRecord> records = new ArrayList<>();
        books.forEach(book -> {
            book.borrowBook();
            records.add(new BorrowedRecord(Identifiers.uuid(), getId(), book.getId(), borrowDate));
        });

        publishEvent(new BookBorrowRecordCreatedEvent(CollectionUtils.transform(records, BorrowedRecord::new)));
    }

    /**
     * 还书
     * @param books 待归还图书
     * @param returnDate 归还日期
     */
    public void returnBooks(List<Book> books, LocalDate returnDate) {
        List<ReturnedRecord> records = new ArrayList<>();
        books.forEach(book -> {
            book.returnBook();
            Optional<BorrowedRecord> borrowingRecord = borrowingBookRecords.stream()
                    .filter(record -> book.getId().equals(record.getBookId())).findFirst();
            borrowingRecord.ifPresent(record -> records.add(new ReturnedRecord(record, returnDate)));
        });

        MonetaryAmount fine = records.stream().reduce(Moneys.of(0), (fee, record) -> record.getOverdueFee().add(fee), (a, b) -> null);

        publishEvent(new BookReturnRecordCreatedEvent( CollectionUtils.transform(records, ReturnedRecordModel::new), fine));
    }

    @OnEvent
    public void onReaderCreated(ReaderCreatedEvent event) {
        this.id = event.getDataModel().getId();
        this.name = event.getDataModel().getName();
        this.unpaidFine = event.getDataModel().getUnpaidFine() == null
                ? Moneys.of(0) : event.getDataModel().getUnpaidFine();
        this.borrowingBookRecords = CollectionUtils.transform(
                event.getDataModel().getBorrowingBookRecords(), BorrowedRecord::new);
    }

    @OnEvent
    private void onBookBorrowRecordCreated(BookBorrowRecordCreatedEvent event) {
        this.borrowingBookRecords.addAll(CollectionUtils.transform(event.getBorrowingRecords(), BorrowedRecord::new));
    }

    @OnEvent
    private void onBookBorrowRecordCreated(BookReturnRecordCreatedEvent event) {
        List<String> returnedRecordIds = CollectionUtils.transform(event.getReturnedRecords(), BorrowedRecordModel::getRecordId);
        borrowingBookRecords.removeIf(borrowingRecord -> returnedRecordIds.contains(borrowingRecord.getRecordId()));

        this.unpaidFine = unpaidFine.add(event.getFine());
    }
}
