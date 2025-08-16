package com.openclassrooms.pay_my_buddy.web.dto.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationOptionDTO {

    private final Integer accountId;
    private final String email;

    public RelationOptionDTO(Integer accountId, String email) {
        this.accountId = accountId;
        this.email = email;
    }
}
