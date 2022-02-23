package com.openclassrooms.paymybuddy.repository;


import com.openclassrooms.paymybuddy.model.BankTransaction;
import org.springframework.data.repository.CrudRepository;

public interface BankTransactionRepository extends CrudRepository<BankTransaction, Long> {
}
