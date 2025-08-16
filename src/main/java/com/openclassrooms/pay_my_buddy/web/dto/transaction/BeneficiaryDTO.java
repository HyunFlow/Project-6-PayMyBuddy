package com.openclassrooms.pay_my_buddy.web.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryDTO {
    private Integer accountId;
    private String username;
    private String email;
}
