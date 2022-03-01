package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_beneficiaries")
public class UserBeneficiary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_beneficiary_id")
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
  @JoinColumn(name = "beneficiary_id")
  @NotNull
  private User beneficiary;



  public UserBeneficiary() {
  }

  public int getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getBeneficiary() {
    return beneficiary;
  }

  public void setBeneficiary(User beneficiary) {
    this.beneficiary = beneficiary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserBeneficiary that = (UserBeneficiary) o;
    return getId() == that.getId() && getUser().equals(that.getUser()) && getBeneficiary().equals(that.getBeneficiary());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUser(), getBeneficiary());
  }

  @Override
  public String toString() {
    return "UserBeneficiary{" +
      "id=" + id +
      ", user=" + user.getEmail() +
      ", beneficiary=" + beneficiary.getEmail() +
      '}';
  }
}
