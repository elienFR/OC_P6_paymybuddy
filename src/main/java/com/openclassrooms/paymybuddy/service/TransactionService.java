package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.configuration.ConstantConfig;
import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.AccountCredit;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.utils.layout.Paging;
import com.openclassrooms.paymybuddy.repository.AccountCreditRepository;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;
  @Autowired
  private AccountCreditRepository accountCreditRepository;

  private static final Logger LOGGER = LogManager.getLogger(TransactionService.class);

  /**
   * This method saves a transaction into the DB
   *
   * @param transaction is the transaction to save.
   * @return the saved transaction, with the correct id.
   */
  @Transactional
  private Transaction saveTransaction(Transaction transaction) {
    LOGGER.info("Saving transaction into DB...");
    return transactionRepository.save(transaction);
  }

  /**
   * Create and save a transaction object into the DB. This transaction is a money transfer between
   * two paymybuddy accounts.
   *
   * @param fromAccount is the account on which the money is taken from.
   * @param toAccount is the account on which the money is transferred to.
   * @param description is an optional description of the transaction.
   * @param amount is the amount of th transaction.
   * @param feesAccount
   * @return the saved transaction into the DB, with its proper id.
   */
  @Transactional
  public Transaction makeATransaction(Account fromAccount,
                                      Account toAccount,
                                      @Nullable String description,
                                      float amount,
                                      Account feesAccount) {
    // ---------- Creating description
    LOGGER.info("Creating description...");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String formattedDateTime = LocalDateTime.now().format(formatter);

    StringBuffer descriptionToApply = new StringBuffer(formattedDateTime + " - ");

    if (description == null || description.isBlank()) {
      descriptionToApply.append("transaction made to " + toAccount.getUser().getEmail());
    } else {
      descriptionToApply.append(description);
    }
    // ---------- END Creating description

    // ---------- Create transaction between account
    LOGGER.info("Creating new Transaction Object...");
    Transaction transactionBetweenAccountsToSave = new Transaction();
    transactionBetweenAccountsToSave.setToAccount(toAccount);
    transactionBetweenAccountsToSave.setFromAccount(fromAccount);
    transactionBetweenAccountsToSave.setAmount(amount);
    transactionBetweenAccountsToSave.setAmount(amount);
    transactionBetweenAccountsToSave.setDescription(descriptionToApply.toString());

    // ----------- Debit
    LOGGER.info("Taking money from "
      + fromAccount.getUser().getEmail()
      + "'s account to prepare transfer...");

    fromAccount.addTransactionFromThisAccount(transactionBetweenAccountsToSave);
    // ----------- Fees
    // Create Fees Transaction only if a fees account exists
    if (feesAccount != null) {
      LOGGER.info("Applying Fees with a percentage of : "
        + Float.parseFloat(ConstantConfig.FEES_PERCENTAGE.getValue())*100f);

      //Create fees description
      StringBuffer feesDescription = new StringBuffer();
      feesDescription.append("Fees for transaction -- ");
      feesDescription.append(descriptionToApply);
      if (description != null && !description.isBlank()) {
        feesDescription.append(" -- to " + toAccount.getUser().getFirstName());
      }
      //Create fees transaction
      LOGGER.info("Creating a new transaction object for Fees...");
      Transaction feesTransactionToSave = applyFees(
        fromAccount,
        feesAccount,
        amount,
        Float.parseFloat(ConstantConfig.FEES_PERCENTAGE.getValue()),
        feesDescription.toString());

      // Associating transaction with accounts
      LOGGER.info("Performing fees money transfer between accounts...");
      fromAccount.addTransactionFromThisAccount(feesTransactionToSave);
      feesAccount.addTransactionToThisAccount(feesTransactionToSave);

      LOGGER.info("Saving fees transaction object to database...");
      saveTransaction(feesTransactionToSave);
    }
    // ----------- END Fees
    // ----------- END Debit

    // ----------- Credit
    LOGGER.info("Adding money to "
      + toAccount.getUser().getEmail()
      + "'s account to finish transfert...");

    toAccount.addTransactionToThisAccount(transactionBetweenAccountsToSave);

    LOGGER.info("Calling for save in DB transaction method...");
    return saveTransaction(transactionBetweenAccountsToSave);
  }

  /**
   * This method create an account credit object, make the money transfer and saves
   * the record into the database. (The money transfer is a POF, it does not really make any REAL
   * money transfer... yet...)
   *
   * @param account is the account the money in transferred to.
   * @param amount is the amount of money transferred to the account.
   * @param description is an optional description for the transfer.
   * @param creditCardNumber is the credit card number from which you want to take money.
   * @param crypto is the credit card cryptogram
   * @param expirationDate is the credit card expiration date.
   * @return a saved account credit object corresponding to the record of this type of transaction
   * into the database.
   */
  @Transactional
  public AccountCredit makeAnAccountCredit(Account account,
                                           float amount,
                                           String description,
                                           String creditCardNumber,
                                           String crypto,
                                           String expirationDate) {
    // ---------- Creating description
    LOGGER.info("Creating description...");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String formattedDateTime = LocalDateTime.now().format(formatter);

    StringBuffer descriptionToApply = new StringBuffer(formattedDateTime + " - ");

    if (description == null || description.isBlank()) {
      descriptionToApply.append("Account credit on : " + account.getUser().getEmail());
    } else {
      descriptionToApply.append(description);
    }
    // ---------- END Creating description

    // ---------- Creating account credit
    LOGGER.info("Creating account credit object...");
    AccountCredit accountCreditToSave = new AccountCredit();
    accountCreditToSave.setAmount(amount);
    accountCreditToSave.setDescription(descriptionToApply.toString());

    //Store credit card number
    StringBuffer creditCardNumberToSave = new StringBuffer();
    for(int i=1; i<=creditCardNumber.length()-4; i++) {
      creditCardNumberToSave.append("*");
    }
    creditCardNumberToSave.append(
      creditCardNumber.substring(creditCardNumber.length()-4,creditCardNumber.length())
    );
    accountCreditToSave.setCreditCardNumber(creditCardNumberToSave.toString());

    accountCreditToSave.setCryptogram(crypto);

    accountCreditToSave.setExpirationDate(expirationDate);

    //Crediting account
    LOGGER.info("Crediting account : " + account.getUser().getEmail());
    account.addAccountCreditToThisAccount(accountCreditToSave);

    LOGGER.info("Calling save in DB account credit method...");
    return saveAccountCredit(accountCreditToSave);
  }

  private AccountCredit saveAccountCredit(AccountCredit accountCreditToSave) {
    LOGGER.info("Saving account credit object in Database...");
    return accountCreditRepository.save(accountCreditToSave);
  }

  /**
   * This method is used to apply fees to a transaction between two paymybuddy accounts.
   *
   * @param fromAccount is the account on which the fees are applied.
   * @param feesAccount is the fees account of pay my buddy app.
   * @param amount corresponds to the amount of money on which fees are applied.
   * @param rate is the fees' rate. i.e : 5% fees is 0.05f rate.
   * @param description is an optional description for the fees.
   * @return is the transaction object saved into the database with its proper id.
   */
  @Transactional
  private Transaction applyFees(Account fromAccount,
                                Account feesAccount,
                                float amount,
                                float rate,
                                @Nullable String description) {
    LOGGER.info("Creating the new fees transaction to save ...");
    Transaction feesToSave = new Transaction();
    feesToSave.setToAccount(feesAccount);
    feesToSave.setFromAccount(fromAccount);
    float feesAmount = amount * rate;
    feesToSave.setAmount(feesAmount);
    feesToSave.setDescription(description);
    LOGGER.info("Calling for save in DB transaction method...");
    return saveTransaction(feesToSave);
  }

  /**
   * This method is used to recover all the transactions made from an account in a paged format.
   *
   * @param pageNumber is the number of the page you want to access to.
   * @param size is the size of the page you want to display.
   * @param account is the account on which you want to get the transactions.
   * @return a paged transaction object.
   */
  public Paged<Transaction> getPageByAccount(int pageNumber, int size, Account account) {
    LOGGER.info("gathering paged transactions from account : " + account.getUser().getEmail()
      + ". Selecting page : " + pageNumber);
    PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));
    Page<Transaction> postPage = transactionRepository.findAllByFromAccountOrToAccount(
      account,
      account,
      request
    );

    return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
  }

}
