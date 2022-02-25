package com.openclassrooms.paymybuddy.integration.repository;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class AccountRepositoryIT {

  @Autowired
  private AccountRepository accountRepository;

  @Test
  public void getAllAccount() {
    //given

    //when
    List<Account> result = new ArrayList<>();
    accountRepository.findAll().forEach(
      account -> result.add(account)
    );

    //then
    assertThat(result).hasSize(2);
  }

}
