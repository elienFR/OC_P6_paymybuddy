package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {
}
