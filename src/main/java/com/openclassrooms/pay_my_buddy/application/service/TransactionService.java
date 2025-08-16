package com.openclassrooms.pay_my_buddy.application.service;

import com.openclassrooms.pay_my_buddy.application.service.transfer.TransferResult;
import com.openclassrooms.pay_my_buddy.domain.model.Account;
import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import com.openclassrooms.pay_my_buddy.domain.model.enums.AccountStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.CurrencyType;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionStatus;
import com.openclassrooms.pay_my_buddy.domain.model.enums.TransactionType;
import com.openclassrooms.pay_my_buddy.domain.repository.AccountRepository;
import com.openclassrooms.pay_my_buddy.domain.repository.TransactionRepository;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionRequest;
import com.openclassrooms.pay_my_buddy.web.dto.transaction.TransactionResponse;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransferResult transferMoney(TransactionRequest dto) {
        final Integer senderAccountId = dto.getSenderAccountId();
        final Integer receiverAccountId = dto.getReceiverAccountId();

        /* Vérifier les identifiants des comptes avant une transaction. */
        if (senderAccountId == null || receiverAccountId == null) {
            throw new IllegalArgumentException(
                "L'identifiant du compte d'émetteur et receveur n'est pas valide.");
        }
        if (senderAccountId.equals(receiverAccountId)) {
            throw new IllegalArgumentException(
                "Vous ne pouvez pas transfer l'argent sur le même compte.");
        }

        final Account senderAccount = accountRepository.findByIdForUpdate(senderAccountId)
            .orElseThrow(() -> new RuntimeException("Invalid sender account"));
        final Account receiverAccount = accountRepository.findByIdForUpdate(receiverAccountId)
            .orElseThrow(() -> new RuntimeException("Invalid receiver account"));

        /* Vérifier les status des comptes sont actifs. */
        if (senderAccount.getAccountStatus() != AccountStatus.ACTIVE
            || receiverAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException("Le compte n'est pas actif.");
        }

        /* Vérifier le montant et la monnaie du virement sont saisis correctement avant une transaction. */
        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        CurrencyType currency = dto.getCurrency();
        if (currency == null) {
            currency = CurrencyType.EUR;
            dto.setCurrency(currency);
        }

        final BigDecimal fee = calculateFee(amount);
        final BigDecimal totalDebit = amount.add(fee);

        /* Vérifier le montant du virement saisi est disponible sur le compte d'émetteur avant une transaction. */
        final BigDecimal before = senderAccount.getBalance();
        if (before.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le solde du compte émetteur est négatif.");
        }
        if (totalDebit.compareTo(before) > 0) {
            BigDecimal shortage = totalDebit.subtract(before).setScale(2, RoundingMode.HALF_UP);

            String msg = "Fonds insuffisants : il manque "
                + shortage.toPlainString() + " " + currency
                + " (Solde " + before.toPlainString() + " " + currency
                + "/ montant " + amount.toPlainString() + " " + currency
                + " + frais " + fee.toPlainString() + " " + currency + ").";

            throw new IllegalArgumentException(msg);
        }

        /* Logique après la validation de la transaction. */
        senderAccount.setBalance(before.subtract(totalDebit));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));

        Transaction tx = new Transaction();
        tx.setSenderAccount(senderAccount);
        tx.setReceiverAccount(receiverAccount);
        tx.setAmount(amount);
        tx.setTransactionFee(fee);
        tx.setCurrency(currency);
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setDescription(dto.getDescription());

        transactionRepository.save(tx);

        return TransferResult.builder()
            .transactionId(tx.getTransactionId())
            .status(tx.getStatus())
            .transactionAmount(amount)
            .transactionFee(fee)
            .currency(currency)
            .senderAccountId(senderAccount.getAccountId())
            .receiverAccountId(receiverAccount.getAccountId())
            .senderBalanceBefore(before)
            .senderBalanceAfter(senderAccount.getBalance())
            .description(dto.getDescription())
            .message("Le virement a été effectué avec succès.")
            .build();
    }

    /* Lire une liste de transaction par un identifiant de compte */
    public List<TransactionResponse> getRecentTransactionsForAccount(Integer accountId) {
        return transactionRepository
            .findTop20BySenderAccount_AccountIdOrderByTransactionTimeDesc(accountId)
            .stream()
            .map(tx -> TransactionResponse.of(tx, accountId))
            .toList();
    }

    /* Calculer un frais de virement. (0.05% du montant) */
    private BigDecimal calculateFee(BigDecimal amount) {
        BigDecimal RATE = new BigDecimal("0.005");
        return amount.multiply(RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
