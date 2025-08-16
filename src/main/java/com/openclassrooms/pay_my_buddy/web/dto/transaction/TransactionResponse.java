package com.openclassrooms.pay_my_buddy.web.dto.transaction;

import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String relationEmail;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime transactionTime;

    public static TransactionResponse of(Transaction tx, Integer currentAccountId) {
        TransactionResponse dto = new TransactionResponse();
        boolean sender = tx.getSenderAccount().getAccountId().equals(currentAccountId);

        dto.relationEmail = sender
            ? tx.getReceiverAccount().getUser().getEmail()
            : tx.getSenderAccount().getUser().getEmail();

        dto.description = tx.getDescription();
        dto.amount      = tx.getAmount();
        dto.currency    = tx.getCurrency() != null ? tx.getCurrency().name() : "EUR";
        dto.transactionTime = tx.getTransactionTime();
        return dto;
    }
}

