package com.openclassrooms.paymybuddy.integration.repository;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryIT {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void dumbTestUsedToExecuteDebugVariableInspection() {
    //given

    //when
    List<User> result = new ArrayList<>();
    userRepository.findAll().forEach(
      user -> result.add(user)
    );

    //then
    assertThat(result).hasSize(5);
  }


}
