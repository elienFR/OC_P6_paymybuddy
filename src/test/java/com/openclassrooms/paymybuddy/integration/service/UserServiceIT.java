package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserBeneficiaryService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {

  @Autowired
  private UserService userService;
  @Autowired
  private UserBeneficiaryService userBeneficiaryService;
  @Autowired
  private TransactionService transactionService;

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
    boolean exists = userService.existsByEmail(givenMail);
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
  public void updatingFirstNameUserTest() {
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

  @Test
  public void removingFirstUserBeneficiariesTest() {
    //given
    Optional<User> givenOptUser = userService.getUserByEmail("admin@email.com");
    User givenUser = givenOptUser.get();
    int expected = givenUser.getUserBeneficiaries().size() - 1;

    givenUser.removeUserBeneficiary(
      givenUser.getUserBeneficiaries().get(0)
    );

    System.out.println("--------------------------------");

    //when
    User result = userService.getUserByEmail("admin@email.com").get();

    List<UserBeneficiary> userBeneficiaryList = new ArrayList<>();
    userBeneficiaryService.getAll().forEach(userBeneficiary -> userBeneficiaryList.add(userBeneficiary));

    //then
    assertThat(result.getUserBeneficiaries().size()).isEqualTo(expected);
    assertThat(userBeneficiaryList.size()).isEqualTo(7);
  }

  @Test
  public void makeATransactionTest() {
    //given
    String givenGiverMail = "admin@email.com";
    User givenGiver = userService.getUserByEmail(givenGiverMail).get();
    String givenReceiverMail = "user@email.com";
    User givenReceiver = userService.getUserByEmail(givenReceiverMail).get();

    float givenAmount = 1200.43F;

    float expectedGiverBalance = givenGiver.getAccount().getBalance() - givenAmount;
    float expectedReceiverBalance = givenReceiver.getAccount().getBalance() + givenAmount;

    int expectedTotalTransaction = 5;

    //when
    userService.makeATransaction(givenGiver, givenReceiver, null, givenAmount);
    List<Transaction> transactionsList = new ArrayList<>();
    transactionService.getAll().forEach(transaction -> {
      transactionsList.add(transaction);
      System.out.println(transaction);
    });

    float resultGiverBalance = givenGiver.getAccount().getBalance();
    float resultReceiverBalance = givenReceiver.getAccount().getBalance();

    //then
    assertThat(transactionsList.size()).isEqualTo(21);
    assertThat(resultGiverBalance).isEqualTo(expectedGiverBalance);
    assertThat(resultReceiverBalance).isEqualTo(expectedReceiverBalance);
  }

  @Test
  public void getAllTransactionFromUserTest() {
    //given
    String givenEmail = "admin@email.com";
    User givenUser = userService.getUserByEmail(givenEmail).get();

    //when
    List<Transaction> result = userService.getAllTransactionFromUser(givenUser);

    //then
    assertThat(result.size()).isEqualTo(12);
  }

  @Test
  public void addBeneficiaryTest() {
    //given
    String givenUserEmail = "user@email.com";
    User givenUser = userService.getUserByEmail(givenUserEmail).get();
    String givenBeneficiaryEmail = "leon@mail.com";

    int sizeBeforeRemoval = givenUser.getUserBeneficiaries().size();
    System.out.println(sizeBeforeRemoval);
    int expected = sizeBeforeRemoval + 1;

    //when
    userService.addBeneficiary(givenUserEmail, givenBeneficiaryEmail);
    int result = givenUser.getUserBeneficiaries().size();
    System.out.println(sizeBeforeRemoval);

    //then
    assertThat(result).isEqualTo(expected);


  }

  @Test
  public void getAllPagedTransactionFromUserTest() {
    String givenUserEmail = "admin@email.com";
    Paged<Transaction> pages = userService.getAllPagedTransactionFromUser(1, 3, givenUserEmail);

    assertThat(pages.getPage().getTotalElements()).isEqualTo(12);
  }

}
