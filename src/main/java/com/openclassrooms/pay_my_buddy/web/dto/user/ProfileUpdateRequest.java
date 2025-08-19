package com.openclassrooms.pay_my_buddy.web.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * DTO de mise à jour du profil.
 * - username/email: champs de base
 * - current/new/confirmPassword: requis seulement en cas de changement de mot de passe
 */
public class ProfileUpdateRequest {

    String username;
    String email;

    String currentPassword;
    String newPassword;
    String confirmPassword;

    public boolean wantsPasswordChange() {
        return (currentPassword != null && !currentPassword.isBlank())
            || (newPassword != null && !newPassword.isBlank())
            || (confirmPassword != null && !confirmPassword.isBlank());
    }
}
