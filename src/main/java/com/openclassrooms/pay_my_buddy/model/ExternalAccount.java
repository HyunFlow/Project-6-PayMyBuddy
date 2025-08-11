package com.openclassrooms.pay_my_buddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "external_accounts")
@Getter
@Setter
@NoArgsConstructor
public class ExternalAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "external_account_id")
  private Integer externalAccountId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "bank_name")
  private String bankName;

  @Column(name = "account_number")
  private String accountNumber;

  @Column(name = "account_holder_name")
  private String accountHolderName;

  @Column(name = "registered_at")
  private LocalDateTime registeredAt;

  @OneToMany(mappedBy = "senderExternalAccount")
  private List<Transaction> sentTransactions;

  @OneToMany(mappedBy = "receiverExternalAccount")
  private List<Transaction> receivedTransactions;

}
