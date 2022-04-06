package com.openclassrooms.paymybuddy.repository;


import com.openclassrooms.paymybuddy.model.BankTransaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankTransactionRepository extends CrudRepository<BankTransaction, Long> {
}
