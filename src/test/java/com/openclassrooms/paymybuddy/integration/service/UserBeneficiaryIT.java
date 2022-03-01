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
    result.forEach(
      userBeneficiary -> System.out.println(userBeneficiary)
    );

    //then
    assertThat(result).hasSize(2);
  }

  @Test
  public void getAllByBeneficiaryTest() {
    //given
    Optional<User> user = userService.getUserByEmail("user@email.com");

    //when
    List<UserBeneficiary> result = userBeneficiaryService.getAllByBeneficiary(user.get());
    result.forEach(
      userBeneficiary -> System.out.println(userBeneficiary)
    );
    //then
    assertThat(result).hasSize(2);
  }

  @Test
  public void saveANewUserBeneficiaryTest() {
    //given
    Optional<User> givenUser = userService.getUserByEmail("leon@mail.com");
    Optional<User> givenBeneficiary = userService.getUserByEmail("admin@email.com");

    UserBeneficiary expected = new UserBeneficiary();
    expected.setUser(givenUser.get());
    expected.setBeneficiary(givenBeneficiary.get());

    //when
    UserBeneficiary result = userBeneficiaryService.save(expected);

    //then
    assertThat(result.getId()).isNotEqualTo(0);
  }

  @Test
  public void removingABeneficiaryTest() {
    //given
    Optional<User> givenOptUser = userService.getUserByEmail("admin@email.com");
    User givenUser = givenOptUser.get();
    //fetching its first beneficiary
    UserBeneficiary givenUserBeneficiary = givenUser.getUserBeneficiaries().get(0);

    int expected = givenUser.getUserBeneficiaries().size()-1;

    //when
    userBeneficiaryService.delete(givenUserBeneficiary);
    givenOptUser = userService.getUserByEmail("admin@email.com");
    int result = userService.getUserByEmail("admin@email.com").get().getUserBeneficiaries().size();

    //then
    assertThat(result).isEqualTo(expected);
  }



}
