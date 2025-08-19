package com.openclassrooms.pay_my_buddy.domain.repository;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
  List<Account> findByUserId(Integer userId);

  @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
  Optional<Account> findByIdPlain(@Param("accountId") Integer accountId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
  Optional<Account> findByIdForUpdate(@Param("accountId") Integer accountId);

  @Query("SELECT a.balance FROM Account a WHERE a.accountId = :accountId")
  BigDecimal getBalanceByAccountId(@Param("accountId") Integer accountId);

  boolean existsByAccountIdAndUserId(Integer accountId, Integer userId);

  Optional<Account> findTopByUserIdOrderByAccountIdAsc(Integer userId);
  Optional<Account> findByUserIdAndAccountType(Integer userId, AccountType accountType);

  @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountStatus = 'ACTIVE'")
  List<Account> findActiveByUserId(@Param("userId") Integer userId);
}
