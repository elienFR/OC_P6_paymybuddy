package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.configuration.ConstantConfig;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;

@Service
@Transactional
public class AccountService {

  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private TransactionService transactionService;
  @Autowired
  private BankTransactionService bankTransactionService;

  /**
   * This method creates a new account with a specified currency code.
   *
   * @param currencyCode it a an enum {@link CurrencyCode}.
   * @return an Account object.
   */
  public Account createNewAccount(CurrencyCode currencyCode) {
    Account newAccount = new Account();
    newAccount.setBalance(0);
    newAccount.setCurrencyCode(currencyCode);
    return newAccount;
  }

  /**
   * This method creates a transaction object thanks to its parameters.
   * And it saves this transaction into the database.
   * A transaction is a move of money between two paymybuddy accounts.
   *
   * @param fromAccount is the account on which the money is taken from.
   * @param toAccount is the account on which the money is provided to.
   * @param description is a description for the transaction
   * @param amount is the amount of the transaction.
   * @param feesAccount it is a boolean used to applied fees or not. The applied percentage
   *                    is set in {@link ConstantConfig}
   * @return a transaction object and saves it in the database.
   */
  public Transaction makeATransaction(
    Account fromAccount,
    Account toAccount,
    @Nullable String description,
    float amount,
    Account feesAccount
  ) {
    return transactionService
      .makeATransaction(fromAccount, toAccount, description, amount, feesAccount);
  }

  /**
   * This method creates and saves a bank transaction into the database.
   * A bank transaction is a transaction of money from a paymybuddy account to an external account.
   *
   * @param account is the paymybuddy account on which the money is being taken from.
   * @param iban is the iban number according to the ISO 13616.
   * @param swiftCode is the swift code according to the ISO 9362:1994.
   * @param description is a description of the transaction.
   * @param amount is the amount of the transaction.
   * @return
   */
  public BankTransaction makeABankTransaction(Account account,
                                              String iban,
                                              String swiftCode,
                                              String description,
                                              float amount) {

    return bankTransactionService.makeABankTransaction(account, iban, swiftCode, description, amount);
  }

  /**
   * This method is used to get all transaction in a paged object.
   *
   * @param pageNumber is the number of the current page you want to extract as a list from the
   *                   paged object.
   * @param size is the size of the page(list) you want to extract from the paged object.
   * @param account is the account concerned by the extraction.
   * @return a paged object.
   */
  public Paged<Transaction> getAllPagedTransaction(int pageNumber, int size, Account account) {
    return transactionService.getPageByAccount(pageNumber, size, account);
  }

  /**
   * This method creates and save an accountCredit object into the database and credit the
   * account from the amount detailed in the method.
   *
   * @param account is the account on which the money is added.
   * @param amount is the amount added to the account.
   * @param description is a description for this transfert.
   * @param creditCardNumber is the credit card number with which you credit you paymybuddy account.
   * @param crypto is the cryptogram number of this credit card.
   * @param expirationDate is the expiration date of this credit card.
   * @return an AccountCredit object saved in the database.
   */
  public AccountCredit makeAccountCredit(Account account,
                                         float amount,
                                         String description,
                                         String creditCardNumber,
                                         String crypto,
                                         String expirationDate) {
    return transactionService
      .makeAnAccountCredit(account, amount, description, creditCardNumber, crypto, expirationDate);
  }
}
