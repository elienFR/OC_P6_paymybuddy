package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AccountService {

  @Autowired
  private AccountRepository accountRepository;

  /** This method creates a new account with a specified currency code   *
   *
   * @param currencyCode
   * @return
   */

  public Account createNewAccount(CurrencyCode currencyCode) {
    Account newAccount = new Account();
    newAccount.setBalance(0);
    newAccount.setCurrencyCode(currencyCode);
    return newAccount;
  }
}
