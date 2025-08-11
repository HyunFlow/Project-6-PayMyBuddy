package com.openclassrooms.pay_my_buddy.service;

import com.openclassrooms.pay_my_buddy.dto.SignupDTO;
import com.openclassrooms.pay_my_buddy.model.Role;
import com.openclassrooms.pay_my_buddy.model.User;
import com.openclassrooms.pay_my_buddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public boolean signup(SignupDTO request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      System.out.println("L'adresse e-mail que vous avez saisie est déjà utilisée.");
      return false;
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setEmail(request.getEmail());
    user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);

    userRepository.save(user);
    return true;
  }
}
