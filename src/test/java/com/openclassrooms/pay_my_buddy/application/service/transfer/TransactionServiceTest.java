package com.openclassrooms.pay_my_buddy.application.service.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.TransactionRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionRequest;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account sender, receiver;

    @BeforeEach
    public void setUp() {
        User senderUser = User.builder()
            .email("sender@email.com").build();

        User receiverUser = User.builder()
            .email("receiver@email.com").build();

        sender = new Account();
        sender.setAccountId(1);
        sender.setUser(senderUser);
        sender.setAccountStatus(AccountStatus.ACTIVE);
        sender.setBalance(new BigDecimal("100.00"));

        receiver = new Account();
        receiver.setUser(receiverUser);
        receiver.setAccountId(2);
        receiver.setAccountStatus(AccountStatus.ACTIVE);
        receiver.setBalance(new BigDecimal("5.00"));
    }

    @Test
    void transferMoney_success_updateBalancesAndDBTransactions() {
        // given
        when(accountRepository.findByIdForUpdate(1)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2)).thenReturn(Optional.of(receiver));

        TransactionRequest req = new TransactionRequest();
        req.setSenderAccountId(1);
        req.setReceiverAccountId(2);
        req.setAmount(new BigDecimal("20.00"));
        req.setCurrency(null);
        req.setDescription("test");

        // when
        TransferResult res = transactionService.transferMoney(req);

        // then
        assertThat(res.getTransactionAmount()).isEqualByComparingTo("20.00");
        assertThat(res.getTransactionFee()).isEqualByComparingTo("0.10");
        assertThat(sender.getBalance()).isEqualByComparingTo("79.90");
        assertThat(receiver.getBalance()).isEqualByComparingTo("25.00");
        assertThat(res.getCurrency()).isEqualTo(CurrencyType.EUR);

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transferMoney_insufficientSenderBalance_throwException() {
        // given
        when(accountRepository.findByIdForUpdate(1)).thenReturn(Optional.of(sender));
        when(accountRepository.findByIdForUpdate(2)).thenReturn(Optional.of(receiver));

        TransactionRequest req = new TransactionRequest();
        req.setSenderAccountId(1);
        req.setReceiverAccountId(2);
        req.setAmount(new BigDecimal("100.00"));

        BigDecimal before = req.getAmount();

        // when
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                () -> transactionService.transferMoney(req));

        // then
        assertTrue(e.getMessage().contains("Fonds insuffisant"));
        assertTrue(e.getMessage().contains("Solde " + before.toPlainString()));
        assertTrue(e.getMessage().contains("/ montant " + req.getAmount().toPlainString()));
        assertTrue(e.getMessage().contains(" + frais 0.50"));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getRecentTransactionsWithAccountId_returnListOfTransactionResponse() {
        // given
        Transaction t1 = new Transaction();
        t1.setTransactionId(1);
        t1.setSenderAccount(sender);
        t1.setReceiverAccount(receiver);
        t1.setAmount(new BigDecimal("4.00"));
        t1.setCurrency(CurrencyType.EUR);

        when(transactionRepository.findTop20BySenderAccount_AccountIdOrderByTransactionTimeDesc(1)).thenReturn(
            List.of(t1));

        // when
        List<TransactionResponse> list = transactionService.getRecentTransactionsForAccount(1);

        // then
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getAmount()).isEqualByComparingTo("4.00");
        assertThat(list.get(0).getCurrency()).isEqualTo("EUR");
    }

}
