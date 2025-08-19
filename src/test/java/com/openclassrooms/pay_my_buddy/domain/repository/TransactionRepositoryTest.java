package com.openclassrooms.pay_my_buddy.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    void requiredColumns_missingAmount_throwsOnSave() {
        Transaction tx = new Transaction();
        tx.setTransactionFee(new BigDecimal("1.00"));
        tx.setCurrency(CurrencyType.EUR);
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setStatus(TransactionStatus.SUCCESS);

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(tx));
    }

    @Test
    void requiredColumns_feeNull_throwsOnSave() {
        Transaction tx = new Transaction();
        tx.setAmount(new BigDecimal("10.00"));
        tx.setTransactionFee(null);
        tx.setCurrency(CurrencyType.EUR);
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setStatus(TransactionStatus.SUCCESS);

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(tx));
    }

    @Test
    void requiredColumns_missingType_throwsOnSave() {
        Transaction tx = new Transaction();
        tx.setAmount(new BigDecimal("10.00"));
        tx.setTransactionFee(new BigDecimal("0.50"));
        tx.setCurrency(CurrencyType.EUR);
        tx.setStatus(TransactionStatus.SUCCESS);

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(tx));
    }

    @Test
    void requiredColumns_missingStatus_throwsOnSave() {
        Transaction tx = new Transaction();
        tx.setAmount(new BigDecimal("10.00"));
        tx.setTransactionFee(new BigDecimal("0.50"));
        tx.setCurrency(CurrencyType.EUR);
        tx.setTransactionType(TransactionType.TRANSFER);

        assertThrows(DataIntegrityViolationException.class, () -> transactionRepository.save(tx));
    }

    @Test
    void currency_defaultsToEUR_whenNull() {
        Transaction tx = new Transaction();
        tx.setAmount(new BigDecimal("10.00"));
        tx.setTransactionFee(new BigDecimal("0.50"));
        tx.setCurrency(null);
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setStatus(TransactionStatus.SUCCESS);

        Transaction saved = transactionRepository.saveAndFlush(tx);
        assertThat(CurrencyType.EUR).isEqualTo(saved.getCurrency());
    }
}
