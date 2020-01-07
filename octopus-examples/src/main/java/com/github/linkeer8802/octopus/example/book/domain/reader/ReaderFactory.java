package com.github.linkeer8802.octopus.example.book.domain.reader;

import com.github.linkeer8802.octopus.core.AbstractAggregateRootFactory;
import com.github.linkeer8802.octopus.core.DomainCreatedEvent;
import com.github.linkeer8802.octopus.core.ModelWithVersion;
import com.github.linkeer8802.octopus.core.util.CollectionUtils;
import com.github.linkeer8802.octopus.example.book.domain.book.BookStatus;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.datamodel.ReaderModel;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.BorrowedRecordEntity;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.entity.ReaderEntity;
import com.github.linkeer8802.octopus.example.book.infrastructure.common.model.event.ReaderCreatedEvent;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.BorrowedRecordEntityRepository;
import com.github.linkeer8802.octopus.example.book.infrastructure.repository.ReaderEntityRepository;
import com.github.linkeer8802.octopus.example.transfer.infrastructure.common.util.Moneys;
import com.github.linkeer8802.octopus.spring.annotation.Factory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Factory
public class ReaderFactory extends AbstractAggregateRootFactory<Reader, ReaderModel> {

    @Resource
    private ReaderEntityRepository readerEntityRepository;
    @Resource
    private BorrowedRecordEntityRepository borrowedRecordEntityRepository;

    @Override
    protected DomainCreatedEvent createByModel(ReaderModel model) {
        return new ReaderCreatedEvent(model);
    }

    @Override
    protected  ModelWithVersion<ReaderModel> loadModel(Serializable id) {
        ReaderEntity readerEntity = readerEntityRepository.findById(id);
        List<BorrowedRecordEntity> records = borrowedRecordEntityRepository
                .findBorrowingRecordByReaderId(readerEntity.getId(), BookStatus.BORROWED.name());

        BigDecimal unpaidFine = readerEntity.getUnpaidFine() == null ? BigDecimal.ZERO : readerEntity.getUnpaidFine();
        ReaderModel readerModel = new ReaderModel(readerEntity.getId(), readerEntity.getName(),
                Moneys.of(unpaidFine), CollectionUtils.transform(records, BorrowedRecordEntity::toDataModel));

        return new ModelWithVersion<>(readerModel, readerEntity.getVersion());
    }

    @Override
    public Integer updateVersion(Serializable id, Long oldVersion, Long newVersion) {
        return readerEntityRepository.updateVersion(id, oldVersion, newVersion);
    }
}
