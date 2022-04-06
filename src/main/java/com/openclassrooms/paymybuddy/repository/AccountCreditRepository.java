package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.AccountCredit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountCreditRepository extends CrudRepository<AccountCredit,Integer> {
}
