package com.openclassrooms.paymybuddy.service;

import com.google.common.hash.Hashing;
import com.openclassrooms.paymybuddy.configuration.ConstantConfig;
import com.openclassrooms.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassrooms.paymybuddy.model.*;
import com.openclassrooms.paymybuddy.model.utils.ClientRegistrationIdName;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.layout.Paged;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);
  private static final String feesAccountMail = ConstantConfig.FEES_ACCOUNT_MAIL.getValue();
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private AccountService accountService;
  @Autowired
  private AuthorityService authorityService;
  @Autowired
  private SpringSecurityConfig springSecurityConfig;
  @Autowired
  private UserBeneficiaryService userBeneficiaryService;
  @Autowired
  private OAuth2Service oAuth2Service;

  /**
   * This method retrieve a user from DB according to its email.
   *
   * @param email is the user's email to be found in DB.
   * @return is the optional saved user from db (contains id) that corresponds to this email.
   */
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * This method saves a user into DB.
   *
   * @param user is the user to be saved.
   * @return is the saved user from db (contains id).
   */
  public User save(User user) {
    LOGGER.info("Saving a user in DB...");
    return userRepository.save(user);
  }

  /**
   * This method returns the existence of a user in the DB according to its email.
   *
   * @param email is the email of the user you want to find the existence.
   * @return true if a user exists with this email, false if it does not.
   */
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * This method creates a new user with its new pay my buddy account
   *
   * @param firstName is the first name of the user you want to create
   * @param lastName  is the last name of the user you want to create
   * @param email     is the email name of the user you want to create
   * @param password  is the password of the user you want to create
   * @return returns the user object instantiated in the database
   */
  public User createAndSaveLocalUser(
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
      newUser.setFromLocal(true);
      newUser.setGithubId(null);
      newUser.setGoogleId(null);

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

  /**
   * This method make a transaction between two paymybuddy accounts
   *
   * @param fromUser    it is the user that pays.
   * @param toUser      it is the user that receive the money.
   * @param description it is an optional description of the transaction.
   * @param amount      it is the amount of the transaction.
   * @param applyFees   it is a boolean if you want to apply fees
   * @return return a transaction object.
   */
  public Transaction makeATransaction(User fromUser,
                                      User toUser,
                                      @Nullable String description,
                                      float amount,
                                      boolean applyFees) {
    LOGGER.info("Starting transaction between two paymybuddy accounts...");
    //This Section is used if you want to apply fees to the transaction
    if (applyFees) {
      return accountService.makeATransaction(
        fromUser.getAccount(),
        toUser.getAccount(),
        description,
        amount,
        getUserByEmail(feesAccountMail).get().getAccount()
      );
    } else {
      return accountService.makeATransaction(
        fromUser.getAccount(),
        toUser.getAccount(),
        description,
        amount,
        null
      );
    }
  }

  /**
   * This method creates and saves a bank transaction into the database.
   * A bank transaction is a transaction of money from a paymybuddy account to an external account.
   *
   * @param user is the paymybuddy user on which the money is being taken from.
   * @param iban is the iban number according to the ISO 13616.
   * @param swiftCode is the swift code according to the ISO 9362:1994.
   * @param description is a description of the transaction.
   * @param amount is the amount of the transaction.
   * @return
   */
  public BankTransaction makeABankTransaction(User user,
                                              String iban,
                                              String swiftCode,
                                              @Nullable String description,
                                              float amount) {
    return accountService.makeABankTransaction(
      user.getAccount(),
      iban,
      swiftCode,
      description,
      amount
    );
  }

  /**
   * This method is used to get all transaction in a paged object.
   *
   * @param pageNumber is the number of the current page you want to extract as a list from the
   *                   paged object.
   * @param size is the size of the page(list) you want to extract from the paged object.
   * @param user is the user concerned by the extraction.
   * @return a paged object.
   */
  public Paged<Transaction> getAllPagedTransactionFromUser(int pageNumber, int size, User user) {
    Account account = user.getAccount();
    return accountService.getAllPagedTransaction(pageNumber, size, account);

  }

  /**
   * This method deletes a user from database.
   *
   * @param user is the user you want to delete in DB.
   */
  public void delete(User user) {
    userRepository.delete(user);
  }


  /**
   * This method creates and save an accountCredit object into the database and credit the
   * account from the amount detailed in the method.
   *
   * @param user is the user on which the money is added.
   * @param amount is the amount added to the account.
   * @param description is a description for this transfert.
   * @param creditCardNumber is the credit card number with which you credit you paymybuddy account.
   * @param crypto is the cryptogram number of this credit card.
   * @param expirationDate is the expiration date of this credit card.
   * @return an AccountCredit object saved in the database.
   */
  public AccountCredit makeAccountCredit(User user, float amount, String description, String creditCardNumber, String crypto, String expirationDate) {
    LOGGER.info("Performing an account credit...");
    return accountService.makeAccountCredit(
      user.getAccount(),
      amount,
      description,
      creditCardNumber,
      crypto,
      expirationDate
    );
  }

  /**
   * This method returns the associated user from database, according to a specific principal
   * (connected user in WebUI)
   *
   * @param loggedInUser is the principal object corresponding to the WebUI connected user.
   * @return the associated user from database.
   * @throws Exception
   */
  public User getUserFromPrincipal(Principal loggedInUser) throws Exception {
    if (loggedInUser instanceof UsernamePasswordAuthenticationToken) {
      return getUserFromUsernamePasswordAuthenticationToken(loggedInUser);

    } else if (loggedInUser instanceof OAuth2AuthenticationToken) {
      return getUserFromOAuth2(loggedInUser);

    } else {
      LOGGER.warn("Cannot handle Principal Object type.");
      throw new IllegalAccessException();

    }

  }

  /**
   * This method creates and saves a principal user that sign hanks to OAuth2 into the DB if
   * it does not already exist.
   *
   * @param firstName is the firstname of the user to be saved.
   * @param lastName is the lastname of the user to be saved.
   * @param email is the email of the user to be saved.
   * @param OAuth2Id is the OAuth2 id of the user to be saved.
   * @param oAuthProvider is the client registration id name of the user to be saved.
   *                      (i.e. : Google, Github...)
   * @return a user properly saved in database.
   */
  private User createAndSaveOAuth2UserInDb(String firstName,
                                           String lastName,
                                           String email,
                                           String OAuth2Id,
                                           ClientRegistrationIdName oAuthProvider) {
    LOGGER.info("Creating a new user to be saved...");
    User userToSaveInDB = new User();
    userToSaveInDB.setFirstName(firstName);
    userToSaveInDB.setLastName(lastName);
    userToSaveInDB.setEmail(email);
    userToSaveInDB.setEnabled(true);
    userToSaveInDB.setFromLocal(false);

    //creating and associating account
    LOGGER.info("Creating and associating a new account to this user...");
    userToSaveInDB.addAccount(
      accountService.createNewAccount(CurrencyCode.EUR)
    );

    LOGGER.info("Fetching ClientRegistrationIdName...");
    if (oAuthProvider.equals(ClientRegistrationIdName.GITHUB)) {
      LOGGER.info("ClientRegistrationIdName is : " + ClientRegistrationIdName.GITHUB.getName());
      userToSaveInDB.setGithubId(OAuth2Id);
      LOGGER.info("Calling save method...");
      return save(userToSaveInDB);

    } else if (oAuthProvider.equals(ClientRegistrationIdName.GOOGLE)) {
      LOGGER.info("ClientRegistrationIdName is : " + ClientRegistrationIdName.GOOGLE.getName());
      userToSaveInDB.setGoogleId(OAuth2Id);
      LOGGER.info("Calling save method...");
      return save(userToSaveInDB);

    } else {
      LOGGER.error("Cannot handle this ClientRegistrationIdName.");
      throw new IllegalArgumentException();

    }
  }


  /**
   * This method retrieves a user into DB a principal made from a username and a password
   * authentication token.
   *
   * @param loggedInUser is the principal to be retrieved in DB.
   * @return is the user object from DB concerned by the principal.
   */
  private User getUserFromUsernamePasswordAuthenticationToken(Principal loggedInUser) {
    LOGGER.info("fetching AuthenticationToken info and retrieving user in local DB.");
    String userEmail = loggedInUser.getName();
    return getUserByEmail(userEmail).get();
  }

  /**
   * This method is a helper method to determine how to retrieve a user into DB from different type
   * of OAuth2 principal WebUI users.
   *
   * @param loggedInUser is the principal to be retrieved in DB.
   * @return is the user object from DB concerned by the principal.
   * @throws Exception
   */
  private User getUserFromOAuth2(Principal loggedInUser) throws Exception {
    if (((OAuth2AuthenticationToken) loggedInUser)
      .getAuthorizedClientRegistrationId().equals(ClientRegistrationIdName.GITHUB.getName())) {

      LOGGER.info("fetching Github info and retrieving user in local DB.");
      return getUserFromOAuth2GitHub(loggedInUser);

    } else if (((OAuth2AuthenticationToken) loggedInUser)
      .getAuthorizedClientRegistrationId().equals(ClientRegistrationIdName.GOOGLE.getName())) {

      LOGGER.info("fetching Google info and retrieving user in local DB.");
      return getUserFromOAuth2Google(loggedInUser);

    } else {
      LOGGER.warn("Do not handle this OAuth2 provider : "
        + ((OAuth2AuthenticationToken) loggedInUser).getAuthorizedClientRegistrationId());
      throw new IllegalAccessException();
    }
  }


  /**
   * This method gets the Database User thanks to the loggedIn user principal information's of an
   * OAuth2 GitHub account.
   *
   * @param loggedInUser It is a principal object, supposed to be an OAuth2AuthenticationToken
   * @return the user from local Db corresponding to the GitHub account used during OAuth2 process
   */
  private User getUserFromOAuth2GitHub(Principal loggedInUser) {
    OAuth2User principal = ((OAuth2AuthenticationToken) loggedInUser).getPrincipal();
    String githubIdSha256 = Hashing.sha256()
      .hashString(principal
          .getAttributes()
          .get("node_id")
          .toString(),
        StandardCharsets.UTF_8).toString();

    String firstName = principal.getAttribute("name");
    if (firstName == null) {
      firstName = principal.getAttribute("login").toString();
    }

    Optional<User> optUser = getDbUserByGithubID(githubIdSha256);

    if (optUser.isPresent()) {
      return optUser.get();
    } else {

//      return newUserForDB;
      return createAndSaveOAuth2UserInDb(
        firstName,
        null,
        null,
        githubIdSha256,
        ClientRegistrationIdName.GITHUB
      );

    }

  }

  /**
   * This method gets the Database User thanks to the loggedIn user principal information's of an
   * OAuth google account.
   *
   * @param loggedInUser It is a principal object, supposed to be an OAuth2AuthenticationToken
   * @return the user from local Db corresponding to the Google account used during OAuth2 process
   */
  private User getUserFromOAuth2Google(Principal loggedInUser) {
    if (loggedInUser instanceof OAuth2AuthenticationToken) {
      LOGGER.info("Retrieving Claims form OpenID connect (oidc) Principal instance.");
      Map<String, Object> claims = oAuth2Service.getOidcClaims((OAuth2AuthenticationToken) loggedInUser);

      String userEmail = claims.get("email").toString();
      String googleId = claims.get("sub").toString();
      String userFirstName = claims.get("given_name").toString();
      String userLastName = claims.get("family_name").toString();

      String sha256hexGoogleId = Hashing.sha256()
        .hashString(googleId, StandardCharsets.UTF_8).toString();

      LOGGER.info("Fetching google user existence in DB.");
      Optional<User> optUser = getDbUserByGoogleId(sha256hexGoogleId);

      if (optUser.isPresent()) {
        LOGGER.info("Google user exists in DB !");
        return optUser.get();
      } else {
        optUser = getUserByEmail(userEmail);

        if (optUser.isPresent()) {
          LOGGER.info("This google user does not exists in DB, but there is already a user with this google's email !");
          LOGGER.info("Associating google id with this local account !");

          User userToUpdate = optUser.get();

          userToUpdate.setGoogleId(sha256hexGoogleId);

          return save(userToUpdate);

        } else {
          LOGGER.info("Google user does not exist in DB, creating it !");

          LOGGER.info("Creating Saving and Returning created user !");
          return createAndSaveOAuth2UserInDb(
            userFirstName,
            userLastName,
            userEmail,
            sha256hexGoogleId,
            ClientRegistrationIdName.GOOGLE
          );
        }
      }
    }
    LOGGER.warn("Principal is not an instance of OAuth2AuthenticationToken.");
    return null;
  }

  /**
   * Recover a user into db according to its GitHub id.
   *
   * @param githubId is the GitHub id of the user you try to recover.
   * @return an optional of the user you are looking for.
   */
  private Optional<User> getDbUserByGithubID(String githubId) {
    return userRepository.findByGithubId(githubId);
  }

  /**
   * Recover a user into db according to its google id.
   *
   * @param googleId is the Google id of the user you try to recover.
   * @return an optional of the user you are looking for.
   */
  private Optional<User> getDbUserByGoogleId(String googleId) {
    return userRepository.findByGoogleId(googleId);
  }
}
