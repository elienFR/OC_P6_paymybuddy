package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.model.utils.Role;
import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "authorities")
public class Authority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "authority_id")
  private int authority_id;

  @Enumerated(EnumType.STRING)
  @Column(name = "name")
  @NotNull
  private Role name;

  @OneToMany(
    mappedBy = "authority",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<UserAuthority> userAuthorities = new ArrayList<>();

  public Authority() {

  }

  public int getAuthority_id() {
    return authority_id;
  }

  public void setAuthority_id(int authority_id) {
    this.authority_id = authority_id;
  }

  public Role getName() {
    return name;
  }

  public void setName(Role name) {
    this.name = name;
  }

  public List<UserAuthority> getUserAuthorities() {
    return userAuthorities;
  }

  public void setUserAuthorities(List<UserAuthority> userAuthorities) {
    this.userAuthorities = userAuthorities;
  }

  @Override
  public String toString() {
    return "Authority{" +
      "authority_id=" + authority_id +
      ", name=" + name +
      '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Authority authority = (Authority) o;
    return getAuthority_id() == authority.getAuthority_id() && getName() == authority.getName();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getAuthority_id(), getName());
  }
}
