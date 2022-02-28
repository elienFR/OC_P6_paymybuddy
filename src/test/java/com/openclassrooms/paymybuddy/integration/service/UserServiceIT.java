package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.Role;
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

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Role role;
  private User newUser = new User();
  private Optional<User> optUser;
  private Account newAccount;

  @BeforeEach
  public void setup() {
    //tested givens
    firstName = "jacky";
    lastName = "smith";
    email = "testmail@mail.com";
    password = bCryptPasswordEncoder.encode("test");
    role = Role.ROLE_USER;

    //tested user
    newUser.setFirstName(firstName);
    newUser.setEmail(email);
    newUser.setPassword(password);
    newUser.setEnabled(true);
    newAccount = new Account();
    newAccount.setBalance(0);
    newAccount.setCurrencyCode(CurrencyCode.EUR);
    newUser.addAccount(newAccount);

    //making sure the user is deleted from database
    userService.getAllByEmail(email).forEach(user ->
      System.out.println(user.getFirstName()));
    userService.deleteByEmail(email);
  }


  @Test
  public void existsTest() {
    //given
    String givenMail = "admin@email.com";
    //when
    boolean exists = userService.exists(givenMail);
    //then
    assertThat(exists).isTrue();
  }

  @Test
  public void createAndSaveUser() {
    //given

    //when
    User result = userService.createAndSaveUser(firstName, lastName, email, password, role);

    //then
    System.out.println(result.getId());
    assertThat(result.getId()).isNotEqualTo(0);

  }


}
