package com.openclassrooms.pay_my_buddy.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "external_accounts")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExternalAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "external_account_id")
  @EqualsAndHashCode.Include
  private Integer externalAccountId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "bank_name", nullable = false)
  private String bankName;

  @Column(name = "account_number", nullable = false, unique = true)
  private String accountNumber;

  @Column(name = "account_holder_name", nullable = false)
  private String accountHolderName;

  @Column(name = "registered_at", insertable = false, updatable = false)
  private LocalDateTime registeredAt;

  @OneToMany(mappedBy = "senderExternalAccount")
  private List<Transaction> sentTransactions;

  @OneToMany(mappedBy = "receiverExternalAccount")
  private List<Transaction> receivedTransactions;

}
