package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AccountService accountService;
  @Autowired
  private UserAuthorityService userAuthorityService;
  @Autowired
  private AuthorityService authorityService;
  @Autowired
  private SpringSecurityConfig springSecurityConfig;
  @Autowired
  private UserBeneficiaryService userBeneficiaryService;


  public Optional<User> getUserByEmail(String email) {
    return userRepository.getByEmail(email);
  }

  public User save(User user) {
    return userRepository.save(user);
  }


  public void deleteByEmail(String email) {
    LOGGER.info("deleting user with email : " + email);
    userRepository.deleteByEmail(email);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * This method creates a new user with its new pay my buddy account
   *
   * @param firstName
   * @param lastName
   * @param email
   * @param password
   * @return
   */
  public User createAndSaveUser(
    String firstName, String lastName, String email, String password, Role role
  ) {
    LOGGER.info("Creating user with email : " + email);
    if (!existsByEmail(email)) {
      //creating newUser
      User newUser = new User();
      newUser.setFirstName(firstName);
      newUser.setLastName(lastName);
      newUser.setEmail(email);
      newUser.setPassword(springSecurityConfig.passwordEncoder().encode(password));
      newUser.setEnabled(true);

      //creating and associating account
      newUser.addAccount(
        accountService.createNewAccount(CurrencyCode.EUR)
      );

      //creating User authorities
      UserAuthority userAuthority = new UserAuthority();
      Authority newAuthority = authorityService.createAndSave(role).get();
      userAuthority.setAuthority(
        newAuthority
      );
      newUser.addUserAuthority(userAuthority);

      return save(newUser);

    } else {
      LOGGER.warn("User's email already exists ! A null user is provided.");
      return null;
    }
  }

  public Transaction makeATransaction(
    User fromUser, User toUser, @Nullable String description, float amount) {
    return accountService.makeATransaction(
      fromUser.getAccount(),
      toUser.getAccount(),
      description,
      amount
    );
  }


  public UserBeneficiary addBeneficiary(String userEmail, String beneficiaryEmail){
    User user = getUserByEmail(userEmail).get();
    User beneficiary = getUserByEmail(beneficiaryEmail).get();
    return userBeneficiaryService.makeBeneficiary(user, beneficiary);
  }

  public List<Transaction> getAllTransactionFromUser(User user) {
    return user.getAccount().getTransactionsFromThisAccount();
  }

  public Paged<Transaction> getAllPagedTransactionFromUser(int pageNumber, int size, String email) {
    Optional<User> optUser = getUserByEmail(email);
    if(optUser.isPresent()) {
      User user = optUser.get();
      Account account = user.getAccount();
      return accountService.getAllPagedTransaction(pageNumber, size, account);
    }
    else {
      LOGGER.warn("No user has got this email !");
      return null;
    }
  }
}
