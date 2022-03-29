package com.openclassrooms.paymybuddy.configuration;

public enum ConstantConfig {

  FEES_ACCOUNT_MAIL("paymybuddyfees@email.com"),
  FEES_PERCENTAGE("0.005");

  private String value;

  ConstantConfig(String value){
    this.value=value;
  }

  public String getValue() {
    return value;
  }
}
