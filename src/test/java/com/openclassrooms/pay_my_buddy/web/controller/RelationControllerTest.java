package com.openclassrooms.pay_my_buddy.web.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.pay_my_buddy.application.service.relation.RelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RelationController.class)
public class RelationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RelationService relationService;

    @Test
    @WithMockUser(username = "user1@email.com", roles = "USER")
    void addRelation_success_redirectsWithMessage() throws Exception {
        mockMvc.perform(post("/relations")
                .param("email", "friend@mail.com")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/relations/new"))
            .andExpect(flash().attribute("success", "Relation ajoutée."));

        verify(relationService).addBeneficiary("user1@email.com", "friend@mail.com");
    }

    @Test
    @WithMockUser(username = "user1@email.com", roles = "USER")
    void addRelation_sameEmailForTarget_redirectsWithErrorMessage() throws Exception {
        doThrow(new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous‑même."))
            .when(relationService).addBeneficiary("user1@email.com", "user1@email.com");

        mockMvc.perform(post("/relations")
                .param("email", "user1@email.com")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/relations/new"))
            .andExpect(flash().attribute("error", "Vous ne pouvez pas vous ajouter vous‑même."));
    }

}
