package com.openclassrooms.pay_my_buddy.config;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.ExternalAccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.TransactionRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRelationRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final TransactionRepository transactionRepository;
  private final UserRelationRepository userRelationRepository;
  private final AccountRepository accountRepository;
  private final ExternalAccountRepository externalAccountRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... args) throws Exception {
    transactionRepository.deleteAllInBatch();
    userRelationRepository.deleteAllInBatch();
    accountRepository.deleteAllInBatch();
    externalAccountRepository.deleteAllInBatch();

    userRepository.deleteAllInBatch();

    // language=MySQL
    jdbcTemplate.execute("ALTER TABLE `users` AUTO_INCREMENT = 1");


    User admin = User.create(
        "admin",
        "admin@mail.com",
        passwordEncoder.encode("admin1234"),
        Role.ADMIN
    );

    userRepository.save(admin);
    Account accAdmin = Account.createDefaultFor(admin);
    accountRepository.save(accAdmin);

    User user1 = User.create(
        "user",
        "user1@mail.com",
        passwordEncoder.encode("user1234"),
        Role.USER
    );

    User user2 = User.create(
        "user",
        "user2@mail.com",
        passwordEncoder.encode("user1234"),
        Role.USER
    );

    userRepository.save(user1);
    userRepository.save(user2);
    Account accUser1 = Account.createDefaultFor(user1);
    Account accUser2 = Account.createDefaultFor(user2);
    accUser1.setBalance(BigDecimal.valueOf(100));
    accUser2.setBalance(BigDecimal.valueOf(1122));
    accountRepository.save(accUser1);
    accountRepository.save(accUser2);



  }
}