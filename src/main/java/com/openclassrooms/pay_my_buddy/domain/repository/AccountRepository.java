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

  /**
   * Récupère un compte par son identifiant sans verrouillage.
   * @param accountId identifiant du compte
   * @return compte si trouvé
   */
  @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
  Optional<Account> findByIdPlain(@Param("accountId") Integer accountId);

  /**
   * Récupère un compte par son identifiant en appliquant un verrou pessimiste d’écriture.
   * À utiliser dans un contexte transactionnel lors de mises à jour de solde.
   * @param accountId identifiant du compte
   * @return compte verrouillé si trouvé
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Account a WHERE a.accountId = :accountId")
  Optional<Account> findByIdForUpdate(@Param("accountId") Integer accountId);

  /**
   * Récupère le solde d’un compte.
   * @param accountId identifiant du compte
   * @return solde courant
   */
  @Query("SELECT a.balance FROM Account a WHERE a.accountId = :accountId")
  BigDecimal getBalanceByAccountId(@Param("accountId") Integer accountId);

  boolean existsByAccountIdAndUserId(Integer accountId, Integer userId);

  Optional<Account> findTopByUserIdOrderByAccountIdAsc(Integer userId);
  Optional<Account> findByUserIdAndAccountType(Integer userId, AccountType accountType);

  /**
   * Liste les comptes actifs d’un utilisateur.
   * @param userId identifiant utilisateur
   * @return comptes actifs
   */
  @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountStatus = 'ACTIVE'")
  List<Account> findActiveByUserId(@Param("userId") Integer userId);
}
