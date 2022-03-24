package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.utils.layout.Paging;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@Service
public class TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  @Transactional
  private Transaction saveTransaction(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  @Transactional
  public Transaction makeATransaction(Account fromAccount,
                                      Account toAccount,
                                      @Nullable String description,
                                      float amount,
                                      Account feesAccount) {
    // ---------- Creating description
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
    Transaction transactionBetweenAccountsToSave = new Transaction();
    transactionBetweenAccountsToSave.setToAccount(toAccount);
    transactionBetweenAccountsToSave.setFromAccount(fromAccount);
    transactionBetweenAccountsToSave.setAmount(amount);
    transactionBetweenAccountsToSave.setAmount(amount);
    transactionBetweenAccountsToSave.setDescription(descriptionToApply.toString());

    // ----------- Debit
    fromAccount.addTransactionFromThisAccount(transactionBetweenAccountsToSave);
    // ----------- Fees
    // Create Fees Transaction only if a fees account exists
    if (feesAccount != null) {
      //Create fees description
      StringBuffer feesDescription = new StringBuffer();
      feesDescription.append("Fees for transaction -- ");
      feesDescription.append(descriptionToApply);
      if(description!= null && !description.isBlank()) {
        feesDescription.append(" -- to " + toAccount.getUser().getFirstName());
      }
      //Create fees transaction
      Transaction feesTransactionToSave = applyFees(
        fromAccount,
        feesAccount,
        amount,
        0.05f,
        feesDescription.toString());

      fromAccount.addTransactionFromThisAccount(feesTransactionToSave);
      feesAccount.addTransactionToThisAccount(feesTransactionToSave);
      saveTransaction(feesTransactionToSave);
    }

    // ----------- Credit
    toAccount.addTransactionToThisAccount(transactionBetweenAccountsToSave);

    return saveTransaction(transactionBetweenAccountsToSave);
  }

  @Transactional
  private Transaction applyFees(Account fromAccount,
                                Account feesAccount,
                                float amount,
                                float rate,
                                String description) {
    Transaction feesToSave = new Transaction();
    feesToSave.setToAccount(feesAccount);
    feesToSave.setFromAccount(fromAccount);
    float feesAmount = amount * rate;
    feesToSave.setAmount(feesAmount);
    feesToSave.setDescription(description);
    return saveTransaction(feesToSave);
  }


  public Iterable<Transaction> getAll() {
    return transactionRepository.findAll();
  }

  public Paged<Transaction> getPage(int pageNumber, int size) {
    PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.ASC, "toAccount"));
    Page<Transaction> postPage = transactionRepository.findAll(request);
    return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
  }

  public Paged<Transaction> getPageByAccount(int pageNumber, int size, Account account) {
    PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.DESC, "id"));

    Page<Transaction> postPage = transactionRepository.findAllByFromAccountOrToAccount(
      account,
      account,
      request
    );


    return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
  }

}
