package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionRepositoryTestIT {

  @Autowired
  private TransactionRepository transactionRepository;

  @Test
  public void getAllTransactionsTest() {
    //given

    //when
    List<Transaction> result = new ArrayList<>();
    transactionRepository.findAll().forEach(
      transaction -> result.add(transaction)
    );

    //then
    assertThat(result).hasSize(4);
  }

}
