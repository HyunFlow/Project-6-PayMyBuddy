package com.openclassrooms.pay_my_buddy.web.controller;

import com.openclassrooms.pay_my_buddy.application.service.AccountService;
import com.openclassrooms.pay_my_buddy.application.service.RelationService;
import com.openclassrooms.pay_my_buddy.application.service.TransactionService;
import com.openclassrooms.pay_my_buddy.application.service.transfer.TransferResult;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    private final AccountService accountService;
    private final RelationService  relationService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @GetMapping("/transfer")
    public String goToDefaultTransfer(Authentication auth) {
        Integer userId = currentUserId(auth);
        Integer accountId = accountService.getDefaultAccountId(userId);
        return "redirect:/accounts/" + accountId + "/transfer";
    }

    @GetMapping("/accounts/{accountId}/transfer")
    public String showTransactionPage(@PathVariable Integer accountId, Model model, Authentication auth) {
        Integer userId = currentUserId(auth);

        if (!accountService.isOwnedByUser(accountId, userId)) {
            throw new AccessDeniedException("Not your account");
        }

        if (!model.containsAttribute("transferForm")) {
            TransactionRequest form = new TransactionRequest();
            form.setSenderAccountId(accountId);
            model.addAttribute("transferForm", form);
        }

        model.addAttribute("accountId", accountId);
        model.addAttribute("relations", relationService.getRelationOptions(accountId));
        model.addAttribute("transactions", transactionService.getRecentTransactionsForAccount(accountId));
        return "transfer";
    }

    @PostMapping("/accounts/{accountId}/transfer")
    public String transfer(@PathVariable Integer accountId,
        @Valid @ModelAttribute("transferForm") TransactionRequest form,
        BindingResult br,
        Authentication auth,
        RedirectAttributes ra) {

        Integer userId = currentUserId(auth);

        if (form.getSenderAccountId() == null || !accountId.equals(form.getSenderAccountId())) {
            br.rejectValue("senderAccountId", "mismatch", "Not your account");
        }

        if (!accountService.isOwnedByUser(accountId, userId)) {
            br.reject("owner", "Not your account");
        }

        if (br.hasErrors()) {
            ra.addFlashAttribute("transferForm", form);
            ra.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "transferForm", br);
            return "redirect:/accounts/" + accountId + "/transfer";
        }

        try {
            TransferResult result = transactionService.transferMoney(form);
            ra.addFlashAttribute("success", "Transfer effectué");
            ra.addFlashAttribute("transferResult", result);
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("transferForm", form);
        }

        return "redirect:/accounts/" + accountId + "/transfer";
    }

    private Integer currentUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"))
            .getId();
    }

}
