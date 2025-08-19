package com.openclassrooms.pay_my_buddy.application.service.account;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Les services d'application pour accéder et valider les {@link Account}s.
 * */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Vérifie qu’un compte interne appartient à un utilisateur donné.
     *
     * @param accountId identifiant du compte interne
     * @param userId identifiant utilisateur
     * @return true si le compte appartient à l’utilisateur
     */
    public boolean isOwnedByUser(Integer accountId, Integer userId) {
        return accountRepository.existsByAccountIdAndUserId(accountId, userId);
    }

    /** Récupère un compte interne par son identifiant. */
    public Account getAccount(Integer accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + accountId));
    }

    /** Récupère l’identifiant du compte interne par défaut (CHECKING) d’un utilisateur. */
    public Integer getDefaultAccountId(Integer userId) {
        return accountRepository
            .findByUserIdAndAccountType(userId, AccountType.CHECKING)
            .map(Account::getAccountId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Le compte n'existe pas pour utilisateur " + userId));
    }
}
