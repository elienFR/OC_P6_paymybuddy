package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
  Page<Transaction> findAllByFromAccountOrToAccount(Account fromAccount, Account toAccount, Pageable page);
}
