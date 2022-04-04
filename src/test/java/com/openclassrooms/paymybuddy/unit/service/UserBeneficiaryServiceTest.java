package com.openclassrooms.paymybuddy.unit.service;

import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.repository.UserBeneficiaryRepository;
import com.openclassrooms.paymybuddy.service.UserBeneficiaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserBeneficiaryServiceTest {

  @Autowired
  private UserBeneficiaryService userBeneficiaryService;

  @MockBean
  private UserBeneficiaryRepository userBeneficiaryRepositoryMocked;

  @Test
  public void saveTest() {
    UserBeneficiary givenUserBeneficiary = new UserBeneficiary();
    when(userBeneficiaryRepositoryMocked.save(givenUserBeneficiary)).thenReturn(givenUserBeneficiary);

    UserBeneficiary result = userBeneficiaryService.save(givenUserBeneficiary);

    assertThat(result).isEqualTo(givenUserBeneficiary);
    verify(userBeneficiaryRepositoryMocked, times(1)).save(givenUserBeneficiary);
  }

  @Test
  public void deleteTest() {
    UserBeneficiary givenUserBeneficiary = new UserBeneficiary();

    userBeneficiaryService.delete(givenUserBeneficiary);

    verify(userBeneficiaryRepositoryMocked, times(1)).delete(givenUserBeneficiary);
  }



}
