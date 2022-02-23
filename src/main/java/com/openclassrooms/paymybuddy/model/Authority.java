package com.openclassrooms.paymybuddy.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Authority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "authority_id")
  private long authority_id;

  @Enumerated(EnumType.STRING)
  @Column(name = "name")
  private Roles name;


}
