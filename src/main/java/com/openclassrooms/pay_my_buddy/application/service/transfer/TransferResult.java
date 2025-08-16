package com.openclassrooms.pay_my_buddy.application.service.transfer;

import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionStatus;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class TransferResult {
    private final Integer transactionId;
    private final TransactionStatus status;
    private final String reasonCode;
    private final String message;

    private final BigDecimal transactionAmount;
    private final BigDecimal transactionFee;
    private final CurrencyType currency;

    private final Integer senderAccountId;
    private final Integer receiverAccountId;

    private final BigDecimal senderBalanceBefore;
    private final BigDecimal senderBalanceAfter;

    private final String description;

}
