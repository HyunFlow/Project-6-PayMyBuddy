package com.openclassrooms.pay_my_buddy.application.service.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.pay_my_buddy.application.service.transfer.TransactionService;
import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.TransactionRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionRequest;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class TransferE2ETest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Rollback
    void transfer_success() {
        User user_a = User.builder()
            .username("testuserA")
            .email("userA@mail.com")
            .password(passwordEncoder.encode("user1234"))
            .role(Role.USER)
            .build();
        userRepository.save(user_a);

        User user_b = User.builder()
            .username("testuserB")
            .email("userB@mail.com")
            .password(passwordEncoder.encode("user1234"))
            .role(Role.USER)
            .build();
        userRepository.save(user_b);

        Account a = new Account();
        a.setUser(user_a);
        a.setBalance(new BigDecimal("500.00"));
        a.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(a);

        Account b = new Account();
        b.setUser(user_b);
        b.setBalance(new BigDecimal("0"));
        b.setAccountStatus(AccountStatus.ACTIVE);
        accountRepository.save(b);

        TransactionRequest req = new TransactionRequest();
        req.setSenderAccountId(user_a.getId());
        req.setReceiverAccountId(user_b.getId());
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency(CurrencyType.EUR);
        req.setDescription("test request");
        transactionService.transferMoney(req);

        Account refreshedA = accountRepository.findById(user_a.getId()).orElseThrow();
        Account refreshedB = accountRepository.findById(user_b.getId()).orElseThrow();

        assertThat(refreshedA.getBalance()).isEqualByComparingTo("399.50");
        assertThat(refreshedB.getBalance()).isEqualByComparingTo("100.00");

        List<Transaction> all = transactionRepository.findAll();
        assertThat(all).hasSize(1);

        Transaction t = all.get(0);
        assertThat(t.getTransactionType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(t.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(t.getAmount()).isEqualByComparingTo("100.00");
        assertThat(t.getTransactionFee()).isEqualByComparingTo("0.50");
        assertThat(t.getSenderAccount().getAccountId()).isEqualTo(a.getAccountId());
        assertThat(t.getReceiverAccount().getAccountId()).isEqualTo(b.getAccountId());
        assertThat(t.getCurrency()).isEqualTo(CurrencyType.EUR);
    }
}
