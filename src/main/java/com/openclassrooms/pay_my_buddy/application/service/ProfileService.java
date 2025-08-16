package com.openclassrooms.pay_my_buddy.application.service;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.user.ProfileUpdateRequest;
import jakarta.transaction.Transactional;
import java.nio.file.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateProfile(Authentication auth, ProfileUpdateRequest req) {
        final User user = getCurrentUser(auth);

        String newName = req.getUsername();

        user.setUsername(newName);

        if (req.wantsPasswordChange()) {
            String cur  = req.getCurrentPassword() == null ? "" : req.getCurrentPassword();
            String npw  = req.getNewPassword() == null ? "" : req.getNewPassword();
            String cfm  = req.getConfirmPassword() == null ? "" : req.getConfirmPassword();

            if (cur.isBlank() || npw.isBlank() || cfm.isBlank()) {
                throw new IllegalArgumentException("Tous les champs du mot de passe sont requis.");
            }
            if (!passwordEncoder.matches(cur, user.getPassword())) {
                throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
            }
            if (!npw.equals(cfm)) {
                throw new IllegalArgumentException("La confirmation du nouveau mot de passe ne correspond pas.");
            }
            if (npw.trim().length() < 8) {
                throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
            }
            if (passwordEncoder.matches(npw, user.getPassword())) {
                throw new IllegalArgumentException("Le nouveau mot de passe est identique à l’ancien.");
            }
            user.setPassword(passwordEncoder.encode(npw.trim()));
        }
    }

    public User getCurrentUser(Authentication auth) {
        if(auth == null || auth.getName() == null) {
            try {
                throw new AccessDeniedException("Authentication required");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

}
