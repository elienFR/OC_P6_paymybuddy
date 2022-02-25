package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT {

  @Autowired
  private UserService userService;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  private User newUser;
  private Optional<User> optUser;
  private Account newAccount;

  @BeforeEach
  public void setup() {
    newUser = new User();
    newUser.setFirstName("jacky");
    newUser.setEmail("testmail@mail.com");
    newUser.setPassword(bCryptPasswordEncoder.encode("test"));
    newUser.setEnabled(true);
    newAccount = new Account();
    newAccount.setBalance(0);
    newAccount.setCurrencyCode(CurrencyCode.EUR);
    newUser.addAccount(newAccount);

    userService.deleteByEmail("testmail@mail.com");
  }


  @Test
  public void getUserByEmailTest() {
    //given
    String givenMail = "admin@email.com";
    //when
    Optional<User> optResult = userService.getUserByEmail(givenMail);
    //then
    assertThat(optResult.isPresent()).isTrue();
  }

  @Test
  public void saveUserTest() {
    //given

    //when
    newUser = userService.save(newUser);
    //then
    assertThat(newUser.getId()).isNotEqualTo(0);
  }

}
