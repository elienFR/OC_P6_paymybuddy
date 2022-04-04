package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.repository.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class BankTransactionService {

  @Autowired
  private BankTransactionRepository bankTransactionRepository;

  /**
   * This method creates a bank transaction and saves it in the database.
   *
   * @param account is the account from which you take the money.
   * @param iban is the iban to which you transfer the money.
   * @param swiftCode is the associated swift code.
   * @param description is a optional description.
   * @param amount is the amount of the bank transaction.
   * @return a bank transaction object and saves it in the database.
   * (No real bank transactions are made yet)
   */
  public BankTransaction makeABankTransaction(Account account,
                                              String iban,
                                              String swiftCode,
                                              @Nullable String description,
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

  /**
   * This method saves a bank transaction object in the database.
   *
   * @param bankTransaction is the bank transaction object you wnt to save into the database.
   * @return the bank transaction object passed but updated with the proper id attributes, that the
   * database assigned to your db entry.
   */
  private BankTransaction saveBankTransaction(BankTransaction bankTransaction) {
    return bankTransactionRepository.save(bankTransaction);
  }
}
