package com.openclassrooms.pay_my_buddy.config;

import com.openclassrooms.pay_my_buddy.model.Role;
import com.openclassrooms.pay_my_buddy.model.User;
import com.openclassrooms.pay_my_buddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.findByEmail("admin@mail.com").isPresent()) {

      userRepository.delete(userRepository.findByEmail("admin@mail.com").get());

      User adminUser = new User();
      adminUser.setUsername("admin");
      adminUser.setEmail("admin@mail.com");
      adminUser.setPassword(passwordEncoder.encode("admin1234"));
      adminUser.setRole(Role.ADMIN);
      userRepository.save(adminUser);

      System.out.println("L'utilisateur admin a été réinitialisé.");
    } else {

      User adminUser = new User();
      adminUser.setUsername("admin");
      adminUser.setEmail("admin@mail.com");
      adminUser.setPassword(passwordEncoder.encode("admin1234"));
      adminUser.setRole(Role.ADMIN);

      userRepository.save(adminUser);

      System.out.println("L'utilisateur admin a été créé avec succès.");
    }
  }
}