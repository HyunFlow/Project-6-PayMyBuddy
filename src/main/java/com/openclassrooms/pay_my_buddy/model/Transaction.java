package com.openclassrooms.pay_my_buddy.model;

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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  private Integer transactionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_account_id")
  private Account senderAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_external_account_id")
  private ExternalAccount senderExternalAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_account_id")
  private Account receiverAccount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "external_receiver_account_id")
  private ExternalAccount receiverExternalAccount;

  private BigDecimal amount;
  @Column(name = "transaction_fee")
  private BigDecimal transactionFee;
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type")
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_status")
  private TransactionStatus status;

  @Column(name = "transaction_time")
  private LocalDateTime transactionTime;

  @Column(name = "external_bank_info")
  private String externalBankInfo;

  private String description;
}
