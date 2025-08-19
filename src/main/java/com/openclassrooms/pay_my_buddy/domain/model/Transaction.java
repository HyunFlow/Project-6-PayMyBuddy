package com.openclassrooms.pay_my_buddy.domain.model;

import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionType;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
/**
 * Entité Transaction couvrant virements internes et opérations externes.
 * Inclut montants, frais, devise, type/statut et horodatage.
 */
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  @EqualsAndHashCode.Include
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
  @JoinColumn(name = "receiver_external_account_id")
  private ExternalAccount receiverExternalAccount;

  @Column(name = "amount", precision = 10, scale = 2, nullable = false)
  private BigDecimal amount;

  @Column(name = "transaction_fee", precision = 10, scale = 2, nullable = false)
  private BigDecimal transactionFee = BigDecimal.ZERO;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", length = 3, nullable = false)
  private CurrencyType currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type", nullable = false)
  private TransactionType transactionType;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_status", nullable = false)
  private TransactionStatus status;

  @CreationTimestamp
  @Column(name = "transaction_time", insertable = false, updatable = false)
  private LocalDateTime transactionTime;

  @Column(name = "external_bank_info")
  private String externalBankInfo;

  private String description;

  @PrePersist
  public void prePersist() {
    if (currency == null) {
      currency = CurrencyType.EUR;
    }
  }

  public void setCurrency(CurrencyType currency) {
    this.currency = (currency == null) ? CurrencyType.EUR : currency;
  }
}
