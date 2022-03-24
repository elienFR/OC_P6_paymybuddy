package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class BankTransactionService {

  @Autowired
  private BankTransactionRepository bankTransactionRepository;


  public BankTransaction makeABankTransaction(Account account,
                                              String iban,
                                              String swiftCode,
                                              String description,
                                              float amount) {

    // ---------- Creating description
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String formattedDateTime = LocalDateTime.now().format(formatter);

    StringBuffer descriptionToApply = new StringBuffer(formattedDateTime + " - ");

    if (description == null || description.isBlank()) {
      descriptionToApply.append("Money transferred to iban : "
        + iban.substring(0, 7)
        + "[****]"
        + iban.substring(iban.length() - 6, iban.length() - 1)
      );
    } else {
      descriptionToApply.append(description);
    }
    // ---------- END Creating description

    // ---------- Create transaction between account
    BankTransaction bankTransactionToSave = new BankTransaction();
    bankTransactionToSave.setDescription(description);
    bankTransactionToSave.setAmount(amount);
    bankTransactionToSave.setExteriorIban(iban);
    bankTransactionToSave.setExteriorBic(swiftCode);

    account.addBankTransactionFromThisAccount(bankTransactionToSave);

    // TODO : implement a swift REAL transaction ?

    return saveBankTransaction(bankTransactionToSave);
  }

  private BankTransaction saveBankTransaction(BankTransaction bankTransaction) {
    return bankTransactionRepository.save(bankTransaction);
  }
}
