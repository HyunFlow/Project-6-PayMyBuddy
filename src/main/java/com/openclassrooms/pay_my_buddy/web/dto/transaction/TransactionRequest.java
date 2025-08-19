package com.openclassrooms.pay_my_buddy.web.dto.transaction;

import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import java.math.BigDecimal;
import lombok.Data;

@Data
/**
 * DTO de requête pour un virement interne.
 * Champs requis côté service: senderAccountId, receiverAccountId, amount.
 * La devise est optionnelle (par défaut EUR si absente).
 */
public class TransactionRequest {
  private Integer sendUserId;
    private Integer senderAccountId;
    private Integer receiverUserId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private CurrencyType currency;
    private String description;
}
