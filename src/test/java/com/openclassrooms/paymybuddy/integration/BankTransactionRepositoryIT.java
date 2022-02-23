package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.model.BankTransaction;
import com.openclassrooms.paymybuddy.repository.BankTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BankTransactionRepositoryIT {

  @Autowired
  private BankTransactionRepository bankTransactionRepository;

  @Test
  public void getAllTransactionsTest() {
    //given

    //when
    List<BankTransaction> result = new ArrayList<>();
    bankTransactionRepository.findAll().forEach(
      bankTransaction -> result.add(bankTransaction)
    );

    //then
    assertThat(result).hasSize(2);
  }
}
