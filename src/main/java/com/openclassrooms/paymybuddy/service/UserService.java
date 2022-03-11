package com.openclassrooms.paymybuddy.service;

import com.google.common.hash.Hashing;
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
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Map;
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
  @Autowired
  private OAuth2Service oAuth2Service;


  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> getDbUserByGithubID(String githubId) {
    return userRepository.findByGithubId(githubId);
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
      newUser.setGithubId("");
      newUser.setGoogleId("");

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

  private User createAndSaveOAuth2UserInDb(
    String firstName, String lastName, String email, String OAuth2Id, ClientRegistrationIdName oAuthProvider) {
    User userToSaveInDB = new User();
    userToSaveInDB.setFirstName(firstName);
    userToSaveInDB.setLastName(lastName);
    userToSaveInDB.setEmail(email);
    userToSaveInDB.setEnabled(true);
    userToSaveInDB.setFromLocal(false);

    if (oAuthProvider.equals(ClientRegistrationIdName.GITHUB)) {
      userToSaveInDB.setGithubId(OAuth2Id);
      return userRepository.save(userToSaveInDB);

    } else if (oAuthProvider.equals(ClientRegistrationIdName.GOOGLE)) {
      userToSaveInDB.setGoogleId(OAuth2Id);
      return userRepository.save(userToSaveInDB);

    } else {
      LOGGER.error("Cannot handle this ClientRegistrationIdName.");
      throw new IllegalArgumentException();

    }
  }

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

  private User getUserFromUsernamePasswordAuthenticationToken(Principal loggedInUser) {
    String userEmail = loggedInUser.getName();
    return getUserByEmail(userEmail).get();
  }

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
    String githubIdSha256 = Hashing.sha256()
      .hashString(
        ((OAuth2AuthenticationToken) loggedInUser)
          .getPrincipal()
          .getAttributes()
          .get("node_id")
          .toString(),
        StandardCharsets.UTF_8).toString();

    Optional<User> optUser = getDbUserByGithubID(githubIdSha256);

    if (optUser.isPresent()) {
      return optUser.get();
    } else {
      User newUserForDB = new User();
      newUserForDB.setFirstName("");
      newUserForDB.setLastName("");
      newUserForDB.setEmail("");
      newUserForDB.setFromLocal(false);
      newUserForDB.setGithubId(githubIdSha256);
      return newUserForDB;
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
    LOGGER.warn("Principal is not an instance of OAuth2AuthenticationToken.");
    return null;
  }

  private Optional<User> getDbUserByGoogleId(String googleId) {
    return userRepository.findByGoogleId(googleId);
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


  public UserBeneficiary addBeneficiary(String userEmail, String beneficiaryEmail) {
    User user = getUserByEmail(userEmail).get();
    User beneficiary = getUserByEmail(beneficiaryEmail).get();
    return userBeneficiaryService.makeBeneficiary(user, beneficiary);
  }

  public List<Transaction> getAllTransactionFromUser(User user) {
    return user.getAccount().getTransactionsFromThisAccount();
  }

  public Paged<Transaction> getAllPagedTransactionFromUser(int pageNumber, int size, User user) {
    Account account = user.getAccount();
    return accountService.getAllPagedTransaction(pageNumber, size, account);

  }
}
