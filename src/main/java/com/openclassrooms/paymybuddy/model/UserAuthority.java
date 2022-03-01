package com.openclassrooms.paymybuddy.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_authority")
public class UserAuthority {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_authority_id")
  private int id;

  @ManyToOne(
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @ManyToOne(
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "authority_id")
  @NotNull
  private Authority authority;

  public UserAuthority() {

  }

  public long getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Authority getAuthority() {
    return authority;
  }

  public void setAuthority(Authority authority) {
    this.authority = authority;
  }

  @Override
  public String toString() {
    return "UserAuthority{" +
      "id=" + id +
      ", user=" + user +
      ", authority=" + authority +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserAuthority that = (UserAuthority) o;
    return getId() == that.getId() && getUser().equals(that.getUser()) && getAuthority().equals(that.getAuthority());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUser(), getAuthority());
  }
}
