package com.openclassrooms.paymybuddy.integration.repository;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserAuthority;
import com.openclassrooms.paymybuddy.repository.UserAuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserAuthorityRepositoryIT {

  @Autowired
  private UserAuthorityRepository userAuthorityRepository;

  @Test
  public void dumbTestUsedToExecuteDebugVariableInspection() {
    //given

    //when
    List<UserAuthority> result = new ArrayList<>();
    userAuthorityRepository.findAll().forEach(
      userAuthority -> result.add(userAuthority)
    );

    //then
    assertThat(result).hasSize(3);
  }

}
