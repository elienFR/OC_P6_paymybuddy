package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.AuthorityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class AuthorityService {

  private static final Logger LOGGER = LogManager.getLogger(AuthorityService.class);
  @Autowired
  private AuthorityRepository authorityRepository;

  private Authority save(Authority authority) {
    return authorityRepository.save(authority);
  }

  /**
   * This method gets an authority from its role's name.
   *
   * @param role is a  {@link Role} Object.
   * @return an optional of Authority from the database.
   */
  public Optional<Authority> getAuthorityByRole(Role role) {
    return authorityRepository.findByName(role);
  }

  /**
   * Checks if a role exists in the database.
   *
   * @param role is a  {@link Role} Object.
   * @return true if the role exists in the database.
   */
  private boolean exists(Role role) {
    return getAuthorityByRole(role).isPresent();
  }

  /**
   * This method creates a new Authority according to its role's name.
   *
   * @param role is a Role Object {@link Role}
   * @return an optional of Authority from the database.
   */
  public Optional<Authority> createAndSave(Role role) {
    //we create an authority, only if it does not already exist.
    if (!exists(role)) {
      Authority authorityToSave = new Authority();
      authorityToSave.setName(role);
      return Optional.of(save(authorityToSave));
    } else {
      LOGGER.warn("Authority Already Exists ! It won't be added to database but the one from DB is given");
      return getAuthorityByRole(role);
    }
  }
}
