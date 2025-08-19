package com.openclassrooms.pay_my_buddy.application.service.relation;

import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.UserRelation;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRelationRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.UserRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.RelationOptionDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final UserRepository userRepository;
    private final UserRelationRepository relationRepository;
    private final AccountRepository accountRepository;

    /**
     * Ajoute une relation de bénéficiaire.
     *
     * @param currentUserEmail email de l’utilisateur courant
     * @param targetEmail email de l’utilisateur à ajouter
     * @throws IllegalArgumentException si l’utilisateur n’existe pas, auto‑ajout ou autre validation échoue
     */
    @Transactional
    public void addBeneficiary(String currentUserEmail, String targetEmail) {
        User me = userRepository.findByEmail(currentUserEmail)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable."));
        User target = userRepository.findByEmail(targetEmail)
            .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur avec cet e-mail."));

        if (me.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous‑même.");
        }
        if (relationRepository.existsByDefiningUserIdAndRelatedUserId(me.getId(), target.getId())) {
            return;
        }
        UserRelation rel = new UserRelation();
        rel.setDefiningUser(me);
        rel.setRelatedUser(target);
        rel.setRelationName("BENEFICIARY");
        relationRepository.save(rel);
    }

    /**
     * Récupère les options de bénéficiaires pour un compte émetteur donné.
     * - Sélectionne, pour chaque relation, un compte interne receveur disponible
     *
     * @param accountId identifiant du compte émetteur
     * @return liste d’options (receiverAccountId, email)
     * @throws IllegalArgumentException si le compte n’existe pas
     */
    @Transactional
    public List<RelationOptionDTO> getRelationOptions(Integer accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("compte introuvable."));

        Integer ownerId = account.getUser().getId();

        List<UserRelation> relations = relationRepository.findAllByDefiningUserId(ownerId);

        return relations.stream()
            .map(rel -> {
                Integer relatedUserId = rel.getRelatedUser().getId();

                Integer receiverAccountId = accountRepository.findByUserIdAndAccountType(relatedUserId, AccountType.CHECKING)
                    .map(Account::getAccountId)
                    .or(() -> accountRepository.findTopByUserIdOrderByAccountIdAsc(relatedUserId)
                        .map(Account::getAccountId))
                    .orElse(null);

                if (receiverAccountId == null) return null;

                String email = rel.getRelatedUser().getEmail();
                return new RelationOptionDTO(receiverAccountId, email);
            })
            .filter(Objects::nonNull)
            .toList();
    }
}
