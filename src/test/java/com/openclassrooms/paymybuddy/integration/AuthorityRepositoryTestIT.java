package com.openclassrooms.paymybuddy.integration;

import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.AuthorityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class AuthorityRepositoryTestIT {

  @Autowired
  private AuthorityRepository authorityRepository;

  @Test
  public void getAllAuthority() {
    //given
    List<Role> expected = new ArrayList<>();
    expected.add(Role.ROLE_ADMIN);
    expected.add(Role.ROLE_USER);

    //when
    List<Role> result = new ArrayList<>();
    authorityRepository.findAll().forEach(
      authority -> result.add(authority.getName())
    );

    //then
    assertThat(result).containsExactlyElementsOf(expected);
  }


}
