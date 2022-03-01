package com.openclassrooms.paymybuddy.integration.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.service.UserBeneficiaryService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserBeneficiaryIT {

  @Autowired
  UserBeneficiaryService userBeneficiaryService;
  @Autowired
  UserService userService;

  @Test
  public void getAllByUserTest() {
    //given
    Optional<User> user = userService.getUserByEmail("user@email.com");

    //when
    List<UserBeneficiary> result = userBeneficiaryService.getAllByUser(user.get());

    //then
    assertThat(result).hasSize(2);
  }

  @Test
  public void getAllByBeneficiaryTest() {
    //given
    Optional<User> user = userService.getUserByEmail("user@email.com");

    //when
    List<UserBeneficiary> result = userBeneficiaryService.getAllByBeneficiary(user.get());

    //then
    assertThat(result).hasSize(2);
  }

}
