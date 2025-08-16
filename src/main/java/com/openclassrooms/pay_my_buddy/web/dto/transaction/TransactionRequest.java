package com.openclassrooms.pay_my_buddy.web.dto.transaction;

import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransactionRequest {
 private Integer sendUserId;
    private Integer senderAccountId;
    private Integer receiverUserId;
    private Integer receiverAccountId;
    private BigDecimal amount;
    private CurrencyType currency;
    private String description;
}
