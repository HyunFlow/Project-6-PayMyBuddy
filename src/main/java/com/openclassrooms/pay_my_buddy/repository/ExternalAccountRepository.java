package com.openclassrooms.pay_my_buddy.repository;

import com.openclassrooms.pay_my_buddy.model.ExternalAccount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Integer> {
  List<ExternalAccount> findByUserId(Integer userId);
}
