package com.openclassrooms.pay_my_buddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  private Integer accountId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type")
  private AccountType accountType;

  @Enumerated(EnumType.STRING)

  @Column(name = "account_status")
  private AccountStatus accountStatus;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "senderAccount")
  private List<Transaction> sentTransactions;

  @OneToMany(mappedBy = "receiverAccount")
  private List<Transaction> receivedTransactions;
}
