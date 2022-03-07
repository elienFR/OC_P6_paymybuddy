package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.utils.layout.Paging;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService {

  @Autowired
  private TransactionRepository transactionRepository;

  private Transaction saveTransaction(Transaction transaction) {
    return transactionRepository.save(transaction);
  }

  public Transaction makeATransaction(
    Account fromAccount, Account toAccount, @Nullable String description, float amount) {
    Transaction transactionToSave = new Transaction();
    transactionToSave.setToAccount(toAccount);
    transactionToSave.setFromAccount(fromAccount);
    transactionToSave.setAmount(amount);
    if (description == null || description.isBlank()) {
      transactionToSave.setDescription(
        "transaction made to " + toAccount.getUser().getEmail() + " the " + LocalDateTime.now()
      );
    } else {
      transactionToSave.setDescription(description);
    }

    fromAccount.addTransactionFromThisAccount(transactionToSave);
    toAccount.addTransactionToThisAccount(transactionToSave);

    return saveTransaction(transactionToSave);
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
    PageRequest request = PageRequest.of(pageNumber - 1, size, Sort.by(Sort.Direction.ASC, "toAccount"));
    Page<Transaction> postPage = transactionRepository.findAllByFromAccount(
      account,
      request
    );
    return new Paged<>(postPage, Paging.of(postPage.getTotalPages(), pageNumber, size));
  }

}
