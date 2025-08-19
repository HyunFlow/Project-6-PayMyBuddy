package com.openclassrooms.pay_my_buddy.domain.repository;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);

  Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);
}
