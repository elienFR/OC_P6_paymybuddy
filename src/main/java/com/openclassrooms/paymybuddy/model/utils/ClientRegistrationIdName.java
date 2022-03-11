package com.openclassrooms.paymybuddy.model.utils;

public enum ClientRegistrationIdName {
  GITHUB("github"),
  GOOGLE("google");

  private String name;

  ClientRegistrationIdName(String name){
    this.name=name;
  }
  public String getName(){
    return name;
  }
}
