package com.openclassrooms.pay_my_buddy.domain.model;

import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  @EqualsAndHashCode.Include
  private Integer accountId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "balance", nullable = false, precision = 10, scale = 2)
  private BigDecimal balance = BigDecimal.ZERO;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type")
  private AccountType accountType;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_status")
  private AccountStatus accountStatus;

  @CreationTimestamp
  @Column(name = "created_at", insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "senderAccount")
  private List<Transaction> sentTransactions;

  @OneToMany(mappedBy = "receiverAccount")
  private List<Transaction> receivedTransactions;

  public static Account createDefaultFor(User user) {
    Account account = new Account();
    account.setUser(user);
    account.setBalance(BigDecimal.ZERO);
    account.setAccountType(AccountType.CHECKING);
    account.setAccountStatus(AccountStatus.ACTIVE);
    return account;
  }
}
