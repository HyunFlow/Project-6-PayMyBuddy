package com.openclassrooms.pay_my_buddy.repository;

import com.openclassrooms.pay_my_buddy.model.Transaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  List<Transaction> findBySenderAccount_AccountId(Integer accountId);
  List<Transaction> findByReceiverAccount_AccountId(Integer accountId);
}
