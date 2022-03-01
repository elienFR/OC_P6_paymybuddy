package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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


}
