package com.openclassrooms.pay_my_buddy.web.controller;

import com.openclassrooms.pay_my_buddy.application.service.account.AccountService;
import com.openclassrooms.pay_my_buddy.application.service.relation.RelationService;
import com.openclassrooms.pay_my_buddy.application.service.transfer.TransactionService;
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
public class TransferController {

    private final AccountService accountService;
    private final RelationService  relationService;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    /**
     * Redirige l’utilisateur vers la page de virement de son compte par défaut.
     * @param auth authentification courante
     * @return redirection vers /accounts/{accountId}/transfer
     */
    @GetMapping("/transfer")
    public String goToDefaultTransfer(Authentication auth) {
        Integer userId = currentUserId(auth);
        Integer accountId = accountService.getDefaultAccountId(userId);
        return "redirect:/accounts/" + accountId + "/transfer";
    }

    /**
     * Affiche la page de virement pour un compte spécifique.
     * - Vérifie la propriété du compte
     * - Prépare le formulaire, les relations disponibles et l’historique récent
     *
     * @param accountId identifiant du compte émetteur
     * @param model modèle vue
     * @param auth authentification courante
     * @return nom de la vue "transfer"
     * @throws AccessDeniedException si le compte n’appartient pas à l’utilisateur
     */
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

    /**
     * Traite un virement depuis le compte indiqué.
     * - Valide la correspondance du compte dans le formulaire
     * - Appelle la logique métier de virement
     * - Gère les erreurs de validation métier sous forme de messages flash
     *
     * @param accountId identifiant du compte émetteur dans l’URL
     * @param form données du virement
     * @param br résultats de validation
     * @param auth authentification courante
     * @param ra attributs de redirection (messages flash)
     * @return redirection vers la page de virement
     */
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
            transactionService.transferMoney(form);
            ra.addFlashAttribute("success", "Transfer effectué");
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            ra.addFlashAttribute("transferForm", form);
        }

        return "redirect:/accounts/" + accountId + "/transfer";
    }

    /**
     * Récupère l’identifiant utilisateur à partir de l’email d’authentification.
     * @param auth authentification courante
     * @return identifiant utilisateur
     * @throws IllegalArgumentException si l’utilisateur est introuvable
     */
    private Integer currentUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"))
            .getId();
    }

}
