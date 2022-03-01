package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.UserBeneficiaryRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT {

  @Autowired
  private UserService userService;
  @Autowired
  private UserBeneficiaryRepository userBeneficiaryRepository;

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
    password = "test";
    role = Role.ROLE_USER;

    //tested user
    newUser.setFirstName(firstName);
    newUser.setEmail(email);
    newUser.setPassword(password);
    newUser.setEnabled(true);
    //set account
    newAccount = new Account();
    newAccount.setBalance(0);
    newAccount.setCurrencyCode(CurrencyCode.EUR);
    newUser.addAccount(newAccount);
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
    assertThat(result.getId()).isNotEqualTo(0);
    assertThat(result.getAccount().getId()).isNotEqualTo(0);
    assertThat(result.getUserAuthorities().size()).isGreaterThan(0);
  }

  @Test
  public void updatingFirstNameUserTest(){
    //given
    String givenNewFirstName = "bobby";
    Optional<User> givenOptUser = userService.getUserByEmail("admin@email.com");
    User givenUser = givenOptUser.get();
    givenUser.setFirstName(givenNewFirstName);

    //when
    User result = userService.save(givenUser);

    //then
    assertThat(result.getFirstName()).isEqualTo(givenNewFirstName);
  }

  @Transactional
  @Test
  public void removingFirstUserBeneficiariesTest() {
    //given
    Optional<User> givenOptUser = userService.getUserByEmail("admin@email.com");
    User givenUser = givenOptUser.get();
    int expected = givenUser.getUserBeneficiaries().size()-1;

    givenUser.removeUserBeneficiary(
      givenUser.getUserBeneficiaries().get(0)
    );

    System.out.println("--------------------------------");

    //when
    User result = userService.getUserByEmail("admin@email.com").get();

    List<UserBeneficiary> userBeneficiaryList = new ArrayList<>();
    userBeneficiaryRepository.findAll().forEach(userBeneficiary -> userBeneficiaryList.add(userBeneficiary));

    //then
    assertThat(givenUser.getUserBeneficiaries().size()).isEqualTo(expected);
    assertThat(userBeneficiaryList.size()).isEqualTo(7);
  }


}
