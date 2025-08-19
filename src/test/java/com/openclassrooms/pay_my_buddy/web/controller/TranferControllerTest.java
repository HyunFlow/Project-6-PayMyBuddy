package com.openclassrooms.pay_my_buddy.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.pay_my_buddy.application.service.account.AccountService;
import com.openclassrooms.pay_my_buddy.application.service.relation.RelationService;
import com.openclassrooms.pay_my_buddy.application.service.transfer.TransactionService;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransferController.class)
public class TranferControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AccountService accountService;
    @MockitoBean
    RelationService relationService;
    @MockitoBean
    TransactionService transactionService;
    @MockitoBean
    UserRepository userRepository;

    private static final String EMAIL = "user@mail.com";
    private static final Integer USER_ID = 100;
    private static final Integer ACCOUNT_ID = 1;

    @BeforeEach
    void setUp() {
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(USER_ID);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser));
        when(accountService.isOwnedByUser(eq(ACCOUNT_ID), eq(USER_ID))).thenReturn(true);
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void post_transfer_success_redirectsWithMessage() throws Exception {
        mvc.perform(post("/accounts/{accountId}/transfer", ACCOUNT_ID)
                .param("senderAccountId", ACCOUNT_ID.toString())
                .param("receiverAccountId", "2")
                .param("amount", "0")
                .param("currency", "EUR")
                .param("description", "test")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts/" + ACCOUNT_ID + "/transfer"))
            .andExpect(flash().attributeExists("success"));

        verify(transactionService).transferMoney(any());
    }

    @Test
    @WithMockUser(username = EMAIL, roles = "USER")
    void post_transfer_serviceThrows_setsErrorFlashAndRedirects() throws Exception {
        doThrow(new IllegalArgumentException("Le montant doit être positif."))
            .when(transactionService).transferMoney(any());

        mvc.perform(post("/accounts/{accountId}/transfer", ACCOUNT_ID)
                .param("senderAccountId", ACCOUNT_ID.toString())
                .param("receiverAccountId", "2")
                .param("amount", "0")
                .param("currency", "EUR")
                .param("description", "test échec")
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/accounts/" + ACCOUNT_ID + "/transfer"))
            .andExpect(flash().attribute("error", "Le montant doit être positif."));

        verify(transactionService).transferMoney(any());
    }

}
