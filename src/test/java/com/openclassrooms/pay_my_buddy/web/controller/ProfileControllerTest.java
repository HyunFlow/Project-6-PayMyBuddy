package com.openclassrooms.pay_my_buddy.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.openclassrooms.pay_my_buddy.application.service.profile.ProfileService;
import com.openclassrooms.pay_my_buddy.web.dto.user.ProfileUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProfileService profileService;

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void post_updateUsername_success_redirectsWithSuccessFlash() throws Exception {
        mvc.perform(post("/profile")
                .param("username", "newName")
                .param("email", "user@email.com")
                .param("currentPassword", "")
                .param("newPassword", "")
                .param("confirmPassword", "")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profile"))
            .andExpect(flash().attribute("success", "Profil mis à jour avec succès"));
    }

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void post_changePassword_wrongCurrentPw_setsFieldError() throws Exception {
        doThrow(new IllegalArgumentException("Le mot de passe actuel est incorrect."))
            .when(profileService).updateProfile(any(), any(ProfileUpdateRequest.class));

        mvc.perform(post("/profile")
                .param("username", "user")
                .param("email", "user@email.com")
                .param("currentPassword", "wrong")
                .param("newPassword", "NewPassword")
                .param("confirmPassword", "NewPassword")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(model().attributeHasFieldErrors("form", "currentPassword"));
    }

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void post_changePassword_wrongConfirmPw_setsFieldError() throws Exception {
        doThrow(new IllegalArgumentException("La confirmation du nouveau mot de passe"))
            .when(profileService).updateProfile(any(), any(ProfileUpdateRequest.class));

        mvc.perform(post("/profile")
                .param("username", "user")
                .param("email", "user@email.com")
                .param("currentPassword", "currentPassword")
                .param("newPassword", "NewPassword")
                .param("confirmPassword", "wrongNewPassword")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));
    }

    @Test
    @WithMockUser(username = "user@email.com", roles = "USER")
    void post_changePassword_sameWithCurrentPw_setsFieldError() throws Exception {
        doThrow(new IllegalArgumentException("Le nouveau mot de passe est identique à l’ancien."))
            .when(profileService).updateProfile(any(), any(ProfileUpdateRequest.class));

        mvc.perform(post("/profile")
                .param("username", "leo")
                .param("email", "user@email.com")
                .param("currentPassword", "currentPassword")
                .param("newPassword", "currentPassword")
                .param("confirmPassword", "NewPassword")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(view().name("profile"))
            .andExpect(model().attributeHasFieldErrors("form", "newPassword"));
    }
}