package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.Account;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIT {

  @Autowired
  private UserService userService;
  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Test
  public void getUserByEmailTest() {
    //given
    String givenMail = "admin@email.com";
    User expected = new User();
    expected.setEmail(givenMail);
    //when
    Optional<User> optResult = userService.getUserByEmail(givenMail);
    User result = optResult.get();

    //then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  public void userCreationTest() {
    User newUser = new User();
    newUser.setFirstName("jacky");
    newUser.setEmail("testmail@mail.com");
    newUser.setPassword(bCryptPasswordEncoder.encode("test"));
    newUser.setEnabled(true);
    userService.save(newUser);
  }

}
