package com.openclassrooms.pay_my_buddy.web.controller;

import com.openclassrooms.pay_my_buddy.application.service.ProfileService;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.web.dto.user.ProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public String showUserProfile(Model model, Authentication auth) {
        User me = profileService.getCurrentUser(auth);
        model.addAttribute("form", new ProfileUpdateRequest(me.getUsername(), me.getEmail(), "","",""));
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@Valid @ModelAttribute("form") ProfileUpdateRequest req, BindingResult br,
        Authentication auth, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "profile";
        }

        try {
            profileService.updateProfile(auth, req);
            ra.addFlashAttribute("success", "Profil mis à jour avec succès");

        } catch (IllegalArgumentException ex) {

            String msg = ex.getMessage();
            if (msg.contains("Le mot de passe actuel")) {
                br.rejectValue("currentPassword", "password.current", msg);
            } else if (msg.contains("La confirmation du nouveau mot de passe")) {
                br.rejectValue("confirmPassword", "password.confirm", msg);
            } else if (msg.contains("Le nouveau mot de passe est identique")) {
                br.rejectValue("newPassword", "password.same", msg);
            } else if (msg.toLowerCase().contains("email")) {
                br.rejectValue("email", "email.duplicate", msg);
            } else {
                br.reject("globalError", msg);
            }

            return "profile";
        }

        return "redirect:/profile";
    }
}
