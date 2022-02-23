package com.openclassrooms.paymybuddy.model;

public enum Roles {
  Admin("ROLE_ADMIN"),
  User("ROLE_USER");

  private String name;

  private Roles(final String name) {
    this.name = name;
  }
}
