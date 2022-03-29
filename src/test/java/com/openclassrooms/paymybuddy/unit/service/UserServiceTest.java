package com.openclassrooms.paymybuddy.unit.service;

import com.openclassrooms.paymybuddy.configuration.ConstantConfig;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.AccountService;
import com.openclassrooms.paymybuddy.service.AuthorityService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

  @Autowired
  private UserService userService;
  @MockBean
  private UserRepository userRepositoryMocked;
  @MockBean
  private AuthorityService authorityServiceMocked;
  @MockBean
  private AccountService accountService;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Role role;
  private User givenUserSetup = new User();
  private Account accountForGivenUser = new Account();

  @BeforeEach
  public void setup() {
    //tested givens
    firstName = "jacky";
    lastName = "smith";
    email = "testmail@mail.com";
    password = "test";
    role = Role.ROLE_USER;
    givenUserSetup.setId(42);
    givenUserSetup.setFirstName(firstName);
    givenUserSetup.setLastName(lastName);
    givenUserSetup.setPassword(password);
    givenUserSetup.setEmail(email);
    givenUserSetup.setEnabled(true);
    givenUserSetup.setFromLocal(false);
    givenUserSetup.setGithubId("SomeGitHubId");
    givenUserSetup.setGoogleId("someGoogleId");

    accountForGivenUser.setId(1);
    accountForGivenUser.setCurrencyCode(CurrencyCode.EUR);
    accountForGivenUser.setBalance(0);
    givenUserSetup.addAccount(accountForGivenUser);
  }

  @Test
  public void getUserByEmailTest() {
    Optional<User> optUser = Optional.of(givenUserSetup);
    when(userRepositoryMocked.findByEmail(email)).thenReturn(optUser);

    User result = userService.getUserByEmail(email).get();

    assertThat(result).isEqualTo(givenUserSetup);
    verify(userRepositoryMocked, times(1)).findByEmail(email);
  }

  @Test
  public void saveTest() {
    when(userRepositoryMocked.save(givenUserSetup)).thenReturn(givenUserSetup);

    User result = userService.save(givenUserSetup);

    assertThat(result).isEqualTo(givenUserSetup);
    verify(userRepositoryMocked, times(1)).save(givenUserSetup);
  }

  @Test
  public void existsByEmailTest() {
    when(userRepositoryMocked.existsByEmail(email)).thenReturn(true);

    boolean result = userService.existsByEmail(email);

    assertThat(result).isTrue();
    verify(userRepositoryMocked, times(1)).existsByEmail(email);
  }


  @Test
  public void createAndSaveTestWithExistingEmail() {
    //given
    boolean expectedExistence = true;

    when(userRepositoryMocked.existsByEmail(email)).thenReturn(expectedExistence);
    //when
    User result = userService.createAndSaveLocalUser(firstName, lastName, email, password, role);

    //then
    assertThat(result).isNull();
  }

  @Test
  public void createAndSaveTestWithNonExistingEmailAndPresentAuthority() {
    //given
    boolean expectedExistence = false;
    Optional<Authority> expectedAuthority = Optional.ofNullable(new Authority());
    User expectedSavedUser = new User();

    when(accountService.createNewAccount(CurrencyCode.EUR)).thenReturn(accountForGivenUser);
    when(userRepositoryMocked.existsByEmail(email)).thenReturn(expectedExistence);
    when(userRepositoryMocked.save(any(User.class))).thenReturn(expectedSavedUser);
    when(authorityServiceMocked.createAndSave(role)).thenReturn(expectedAuthority);
    //when
    User result = userService.createAndSaveLocalUser(firstName, lastName, email, password, role);

    //then
    assertThat(result).isEqualTo(expectedSavedUser);
  }

  @Test
  public void makeATransactionTestWithoutFees() {
    Account accountForSecondGivenUser = new Account();
    accountForSecondGivenUser.setId(2);

    User secondGivenUser = new User();
    secondGivenUser.setId(28);
    secondGivenUser.setEmail("email number 2");
    secondGivenUser.addAccount(accountForSecondGivenUser);

    float amount = 28.28f;
    String description = "some description";
    boolean fees = false;

    Transaction expectedTransaction = new Transaction();
    expectedTransaction.setFromAccount(accountForGivenUser);
    expectedTransaction.setToAccount(accountForSecondGivenUser);
    expectedTransaction.setId(1);
    expectedTransaction.setAmount(amount);
    expectedTransaction.setDescription(description);

    when(accountService.makeATransaction(givenUserSetup.getAccount(),
      secondGivenUser.getAccount(), description, amount, null))
      .thenReturn(expectedTransaction);

    Transaction result = userService
      .makeATransaction(givenUserSetup, secondGivenUser, description, amount, fees);

    assertThat(result).isEqualTo(expectedTransaction);

  }

  @Test
  public void makeATransactionTestWithFees() {
    Account accountForSecondGivenUser = new Account();
    accountForSecondGivenUser.setId(2);

    User secondGivenUser = new User();
    secondGivenUser.setId(28);
    secondGivenUser.setEmail("email number 2");
    secondGivenUser.addAccount(accountForSecondGivenUser);

    float amount = 28.28f;
    String description = "some description";
    boolean fees = true;

    User feesUser = new User();
    Account feesAccount = new Account();
    feesAccount.setId(999);
    feesUser.addAccount(feesAccount);


    Transaction expectedTransaction = new Transaction();
    expectedTransaction.setFromAccount(accountForGivenUser);
    expectedTransaction.setToAccount(accountForSecondGivenUser);
    expectedTransaction.setId(1);
    expectedTransaction.setAmount(amount);
    expectedTransaction.setDescription(description);

    when(userService
      .getUserByEmail(ConstantConfig.FEES_ACCOUNT_MAIL.getValue()))
      .thenReturn(Optional.of(feesUser));
    when(accountService
      .makeATransaction(givenUserSetup
        .getAccount(), secondGivenUser.getAccount(), description, amount, feesAccount))
      .thenReturn(expectedTransaction);

    Transaction result = userService
      .makeATransaction(givenUserSetup, secondGivenUser, description, amount, fees);

    assertThat(result).isEqualTo(expectedTransaction);

  }

  @Test
  public void makeABankTransactionTest() {
    String givenIban = "someIban";
    String givenBic = "someBic";
    String description = "someDescription";
    float amount = 42f;

    BankTransaction expected = new BankTransaction();
    expected.setId(1);

    when(accountService
      .makeABankTransaction(givenUserSetup.getAccount(), givenIban, givenBic, description, amount))
      .thenReturn(expected);

    BankTransaction result = userService
      .makeABankTransaction(givenUserSetup, givenIban, givenBic, description, amount);

    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void getAllPagedTransactionFromUserTest() {
    Paged<Transaction> transactionPaged = userService
      .getAllPagedTransactionFromUser(42, 90, givenUserSetup);

    verify(accountService, times(1))
      .getAllPagedTransaction(42, 90, accountForGivenUser);
  }

  @Test
  public void deleteTest() {
    userService.delete(givenUserSetup);
    verify(userRepositoryMocked, times(1)).delete(givenUserSetup);
  }

  @Test
  public void makeAccountCreditTest() {
    String givenCreditCardNumber = "someCreditCardNumber";
    String givenCrypto = "someCrypto";
    String givenExpiration = "someExpiration";
    String givenDescription = "someDescription";
    float amount = 42f;

    AccountCredit expected = new AccountCredit();
    expected.setId(1);

    when(accountService
      .makeAccountCredit(givenUserSetup.getAccount(), amount, givenDescription, givenCreditCardNumber, givenCrypto, givenExpiration))
      .thenReturn(expected);

    AccountCredit result = userService
      .makeAccountCredit(givenUserSetup, amount, givenDescription, givenCreditCardNumber, givenCrypto, givenExpiration);

    assertThat(result).isEqualTo(expected);
  }

}
