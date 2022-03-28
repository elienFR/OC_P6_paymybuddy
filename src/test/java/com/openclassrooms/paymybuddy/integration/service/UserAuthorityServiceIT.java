package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.UserAuthority;
import com.openclassrooms.paymybuddy.service.UserAuthorityService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql({"classpath:test-schema.sql", "classpath:test-data.sql"})
@Transactional
public class UserAuthorityServiceIT {

  @Autowired
  private UserAuthorityService userAuthorityService;
  @Autowired
  private UserService userService;

  @Test
  public void testGetByEmailIntegration() {
    List<UserAuthority> userAuthorities = userAuthorityService.getUserAuthorities(
        userService.getUserByEmail("admin@email.com").get()
      );
    assertThat(userAuthorities).hasSize(2);
  }

}
