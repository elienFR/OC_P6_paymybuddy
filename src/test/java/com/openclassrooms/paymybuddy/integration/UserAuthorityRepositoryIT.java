package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.model.UserAuthority;
import com.openclassrooms.paymybuddy.repository.UserAuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserAuthorityRepositoryIT {

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  @Test
  public void dumbTestUsedToExecuteDebugVariableInspection() {
    Iterable<UserAuthority> userAuthorities = userAuthorityRepository.findAll();
    System.out.println("test");
  }

}
