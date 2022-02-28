package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.AuthorityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
   * This method get an authority from its role's name.
   *
   * @param role
   * @return
   */
  public Optional<Authority> getAuthorityByRole(Role role) {
    return authorityRepository.findByName(role);
  }

  /**
   * This method returns a list of authorities from a list of roles.
   *
   * @param roles
   * @return
   */
  public List<Authority> getAuthorityByRole(List<Role> roles) {
    List<Authority> authorities = new ArrayList<>();
    roles.forEach(role ->
      authorities.add(getAuthorityByRole(role).get())
    );
    return authorities;
  }

  private boolean exists(Role role) {
    return getAuthorityByRole(role).isPresent();
  }

  /**
   * This method creates a new Authority according to its role's name.
   *
   * @param role
   * @return
   */
  public Optional<Authority> createAndSave(Role role) {
    //we create an authority, only if it does not already exist.
    if (!exists(role)) {
      Authority authorityToSave = new Authority();
      authorityToSave.setName(role);
      return Optional.of(save(authorityToSave));
    } else {
      LOGGER.warn("Authority Already Exists ! It won't be added to database");
      return Optional.empty();
    }
  }
}
