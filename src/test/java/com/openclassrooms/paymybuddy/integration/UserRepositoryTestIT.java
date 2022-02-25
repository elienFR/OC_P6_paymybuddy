package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTestIT {

  @Autowired
  private UserRepository userRepository;

  @Test
  public void dumbTestUsedToExecuteDebugVariableInspection() {
    Iterable<User> userIterable = userRepository.findAll();
    System.out.println("test");
  }


}
