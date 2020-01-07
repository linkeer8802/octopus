package com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.money.MonetaryAmount;
import java.util.List;

/**
 * @author weird
 * @date 2019/12/16
 */
@Data
@AllArgsConstructor
public class ReaderModel {
    private String id;
    private String name;
    private MonetaryAmount unpaidFine;
    private List<BorrowedRecordModel> borrowingBookRecords;
}
