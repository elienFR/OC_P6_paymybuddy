package com.openclassrooms.paymybuddy.model;


import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private long id;

  @Column(name = "first_name")
  @NotNull
  @Size(max = 50)
  private String firstName;

  @Column(name = "last_name")
  @Size(max = 50)
  private String lastName;

  @Column(name = "email")
  @NotNull
  @Size(max = 50)
  private String email;

  @Column(name = "password")
  @NotNull
  @Size(max = 60)
  private String password;

  @Column(name = "enabled")
  @NotNull
  @Size(min = 1, max = 1)
  private boolean enabled;

  @OneToMany(
    mappedBy = "user",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<UserAuthority> userAuthorities = new ArrayList<>();

  @OneToOne(
    mappedBy = "user",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private Account account;

  public User() {

  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public List<UserAuthority> getUserAuthorities() {
    return userAuthorities;
  }

  public void setUserAuthorities(List<UserAuthority> userAuthorities) {
    this.userAuthorities = userAuthorities;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return getId() == user.getId() && isEnabled() == user.isEnabled() && getFirstName().equals(user.getFirstName()) && Objects.equals(getLastName(), user.getLastName()) && getEmail().equals(user.getEmail()) && getPassword().equals(user.getPassword());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFirstName(), getLastName(), getEmail(), getPassword(), isEnabled());
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", firstName='" + firstName + '\'' +
      ", lastName='" + lastName + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", enabled=" + enabled +
      '}';


  }
}
