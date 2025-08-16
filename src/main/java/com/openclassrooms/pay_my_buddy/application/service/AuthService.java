package com.openclassrooms.pay_my_buddy.application.service;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.auth.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public boolean signup(SignupRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
    }

    User user = User.create(
        request.getUsername(),
        request.getEmail(),
        bCryptPasswordEncoder.encode(request.getPassword()),
        Role.USER
    );
    userRepository.save(user);

    Account account = Account.createDefaultFor(user);
    accountRepository.save(account);

    return true;
  }
}
