package com.openclassrooms.pay_my_buddy.application.service.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.openclassrooms.pay_my_buddy.application.service.authentication.AuthService;
import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.auth.SignupRequest;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
public class SignupE2ETest {

    @Autowired
    AuthService authService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;

    @Test
    @Transactional
    @Rollback
    void signup_success_createsUserAndDefaultAccount() {
        // given
        String email = "user@email.com";

        // when
        SignupRequest request = new SignupRequest();
        request.setUsername("user");
        request.setEmail(email);
        request.setPassword("password");

        boolean ok = authService.signup(request);

        // then
        assertThat(ok).isTrue();

        User user = userRepository.findByEmail(email).orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.USER);

        List<Account> accounts = accountRepository.findActiveByUserId(user.getId());
        assertThat(accounts).hasSize(1);
        Account acc = accounts.get(0);
        assertThat(acc.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(acc.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(acc.getBalance()).isEqualByComparingTo(new BigDecimal("0.00"));
    }

    @Test
    @Transactional
    @Rollback
    void signup_duplicateEmail_returnsFalse_andNoExtraAccount() {
        // given
        String email = "user_a@email.com";

        SignupRequest first_request = new SignupRequest();
        first_request.setUsername("user_a");
        first_request.setEmail(email);
        first_request.setPassword("password");

        boolean first = authService.signup(first_request);
        assertThat(first).isTrue();
        User user = userRepository.findByEmail(email).orElseThrow();
        int beforeAccounts = accountRepository.findActiveByUserId(user.getId()).size();

        // when

        SignupRequest second_request = new SignupRequest();
        second_request.setUsername("user_b");
        second_request.setEmail(email);
        second_request.setPassword("password");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> authService.signup(second_request));

        // then
        assertTrue(e.getMessage().contains("Cet e-mail est déjà utilisé"));
        assertThat(userRepository.findByEmail(email)).isPresent();
        assertThat(accountRepository.findActiveByUserId(user.getId())).hasSize(beforeAccounts);
    }

}
