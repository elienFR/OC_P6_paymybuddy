package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.configuration.SpringSecurityConfig;
import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserAuthority;
import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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


  public Optional<User> getUserByEmail(String email) {
    return userRepository.getByEmail(email);
  }

  private User save(User user) {
    return userRepository.save(user);
  }


  public void deleteByEmail(String email) {
    LOGGER.info("deleting user with email : " + email);
    userRepository.deleteByEmail(email);
  }

  public boolean exists(String email) {
    return getUserByEmail(email).isPresent();
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
    if (!exists(email)) {
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
}
