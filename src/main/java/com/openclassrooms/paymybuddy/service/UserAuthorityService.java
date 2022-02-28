package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserAuthority;
import com.openclassrooms.paymybuddy.repository.UserAuthorityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserAuthorityService {

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  private static final Logger LOGGER = LogManager.getLogger(UserAuthorityService.class);


  /**
   * This method gather all the authorities of a specific user
   *
   * @param newUser
   * @return a list of authorities
   */
  public List<UserAuthority> getUserAuthorities(User newUser) {
    List<UserAuthority> userAuthorities = new ArrayList<>();

    userAuthorityRepository.findAllByUser(newUser).forEach(
      userAuthorities::add);

    return userAuthorities;
  }

}
