package com.openclassrooms.paymybuddy.unit.service;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.utils.Role;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import com.openclassrooms.paymybuddy.service.AuthorityService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

  @Autowired
  private UserService userService;
  @MockBean
  private UserRepository userRepositoryMocked;
  @MockBean
  private AuthorityService authorityServiceMocked;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Role role;

  @BeforeEach
  public void setup() {
    //tested givens
    firstName = "jacky";
    lastName = "smith";
    email = "testmail@mail.com";
    password = "test";
    role = Role.ROLE_USER;
  }

  @Test
  public void createAndSaveTestWithExistingEmail() {
    //given
    boolean expectedExistence = true;

    when(userRepositoryMocked.existsByEmail(email)).thenReturn(expectedExistence);
    //when
    User result = userService.createAndSaveLocalUser(firstName, lastName, email, password, role);

    //then
    assertThat(result).isNull();
  }

  @Test
  public void createAndSaveTestWithNonExistingEmailAndPresentAuthority() {
    //given
    boolean expectedExistence = false;
    Optional<User> expectedOptUser = Optional.empty();
    Optional<Authority> expectedAuthority = Optional.ofNullable(new Authority());
    User expectedSavedUser = new User();

    when(userRepositoryMocked.existsByEmail(email)).thenReturn(expectedExistence);
    when(userRepositoryMocked.save(any(User.class))).thenReturn(expectedSavedUser);
    when(authorityServiceMocked.createAndSave(role)).thenReturn(expectedAuthority);
    //when
    User result = userService.createAndSaveLocalUser(firstName, lastName, email, password, role);

    //then
    assertThat(result).isEqualTo(expectedSavedUser);
  }

  @Test
  public void deleteByEmailTest() {
    //given

    //when
    userService.deleteByEmail(email);

    //then
    verify(userRepositoryMocked, Mockito.times(1)).deleteByEmail(email);
  }

  @Test
  public void existsTest() {
    //Given
    boolean expected = true;
    when(userRepositoryMocked.existsByEmail(email)).thenReturn(expected);

    //when
    boolean result = userService.existsByEmail(email);

    //then
    assertThat(result).isTrue();
  }



}
