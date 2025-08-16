package com.openclassrooms.pay_my_buddy.web.dto.relation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewRelationRequest {
    @Email
    @NotBlank
    private String email;
}
