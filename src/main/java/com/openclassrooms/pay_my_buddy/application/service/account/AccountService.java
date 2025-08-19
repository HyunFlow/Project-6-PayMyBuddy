package com.openclassrooms.pay_my_buddy.application.service.account;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Un service d'application pour accéder et valider les {@link Account}s.
 * */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Vérifier qu'un compte interne d'application appartient à un utilisateur spécifique.
     *
     * @param accountId : identifiant du compte interne
     * @param userId
     * @return boolean : le résultat de possession
     */
    public boolean isOwnedByUser(Integer accountId, Integer userId) {
        return accountRepository.existsByAccountIdAndUserId(accountId, userId);
    }

    /**
     * Récupérer un compte interne correspond par un @param accountId.
     */
    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + accountId));
    }

    /**
     * Récupérer un identifiant du compte interne (CHECKING) par défaut qui correspond à @param userId.
     */
    public Integer getDefaultAccountId(Integer userId) {
        return accountRepository
            .findByUserIdAndAccountType(userId, AccountType.CHECKING)
            .map(Account::getAccountId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Le compte n'existe pas pour utilisateur " + userId));
    }
}
