package com.openclassrooms.pay_my_buddy.web.dto.relation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
/**
 * DTO de requête pour l’ajout d’une relation bénéficiaire.
 * Champ requis: email (format valide, non vide).
 */
public class NewRelationRequest {
    @Email(message = "Adresse e-mail invalide")
    @NotBlank
    private String email;
}
