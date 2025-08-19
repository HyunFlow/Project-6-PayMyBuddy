package com.openclassrooms.pay_my_buddy.domain.repository;

import com.openclassrooms.pay_my_buddy.domain.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  /**
   * Liste les transactions où le compte est émetteur.
   */
  List<Transaction> findBySenderAccount_AccountId(Integer accountId);
  /**
   * Liste les transactions où le compte est receveur.
   */
  List<Transaction> findByReceiverAccount_AccountId(Integer accountId);
  /**
   * Top 20 transactions les plus récentes (émetteur).
   */
  List<Transaction> findTop20BySenderAccount_AccountIdOrderByTransactionTimeDesc(Integer accountId);
  /**
   * Top 20 transactions les plus récentes (receveur).
   */
  List<Transaction> findTop20ByReceiverAccount_AccountIdOrderByTransactionTimeDesc(Integer accountId);
  /**
   * Top 20 transactions récentes où le compte est émetteur OU receveur.
   */
  List<Transaction> findTop20BySenderAccount_AccountIdOrReceiverAccount_AccountIdOrderByTransactionTimeDesc
      (Integer senderAccountId, Integer receiverAccountId);
}
