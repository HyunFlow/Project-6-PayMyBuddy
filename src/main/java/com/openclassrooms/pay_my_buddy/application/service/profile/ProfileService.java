package com.openclassrooms.pay_my_buddy.application.service.profile;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.user.ProfileUpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Met à jour le profil utilisateur.
     * - Modifie le nom d’utilisateur
     * - En cas de changement de mot de passe: vérifie l’actuel, la confirmation et la longueur, puis chiffre et sauvegarde
     *
     * @param auth informations d’authentification courante (basée sur l’email)
     * @param req nom, email et champs liés au mot de passe
     * @throws IllegalArgumentException si les validations de mot de passe échouent
     */
    @Transactional
    public void updateProfile(Authentication auth, ProfileUpdateRequest req) {
        final User user = getCurrentUser(auth);

        String newName = req.getUsername();
        user.setUsername(newName);

        if (req.wantsPasswordChange()) {
            String cur = req.getCurrentPassword() == null ? "" : req.getCurrentPassword();
            String npw = req.getNewPassword() == null ? "" : req.getNewPassword();
            String cfm = req.getConfirmPassword() == null ? "" : req.getConfirmPassword();

            if (cur.isBlank() || npw.isBlank() || cfm.isBlank()) {
                throw new IllegalArgumentException("Tous les champs du mot de passe sont requis.");
            }
            if (!passwordEncoder.matches(cur, user.getPassword())) {
                throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
            }
            if (!npw.equals(cfm)) {
                throw new IllegalArgumentException(
                    "La confirmation du nouveau mot de passe ne correspond pas.");
            }
            if (npw.trim().length() < 8) {
                throw new IllegalArgumentException(
                    "Le nouveau mot de passe doit contenir au moins 8 caractères.");
            }
            if (passwordEncoder.matches(npw, user.getPassword())) {
                throw new IllegalArgumentException(
                    "Le nouveau mot de passe est identique à l’ancien.");
            }
            user.setPassword(passwordEncoder.encode(npw.trim()));
        }
    }

    /**
     * Récupère l’utilisateur courant à partir de l’authentification.
     * @param auth objet d’authentification
     * @return l’utilisateur courant
     * @throws AuthenticationCredentialsNotFoundException si l’authentification est absente
     * @throws RuntimeException si aucun utilisateur n’est trouvé
     */
    public User getCurrentUser(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

}
