package com.openclassrooms.paymybuddy.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "account_credits")
public class AccountCredit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_credits_id")
  private int id;

  @Column(name = "amount")
  @NotNull
  private float amount;

  @Column(name = "description")
  private String description;

  @Column(name = "credit_card_number")
  @NotNull
  private String creditCardNumber;

  @Column(name = "crypto")
  @NotNull
  private String cryptogram;

  @Column(name = "expiration_Date")
  @NotNull
  private String expirationDate;

  @ManyToOne(
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "paymybuddy_account")
  @NotNull
  private Account account;

  public AccountCredit() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public float getAmount() {
    return amount;
  }

  public void setAmount(float amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreditCardNumber() {
    return creditCardNumber;
  }

  public void setCreditCardNumber(String creditCardNumber) {
    this.creditCardNumber = creditCardNumber;
  }

  public String getCryptogram() {
    return cryptogram;
  }

  public void setCryptogram(String cryptogram) {
    this.cryptogram = cryptogram;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountCredit that = (AccountCredit) o;
    return getId() == that.getId() && Float.compare(that.getAmount(), getAmount()) == 0 && Objects.equals(getDescription(), that.getDescription()) && getCreditCardNumber().equals(that.getCreditCardNumber()) && getCryptogram().equals(that.getCryptogram()) && getExpirationDate().equals(that.getExpirationDate()) && getAccount().equals(that.getAccount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getAmount(), getDescription(), getCreditCardNumber(), getCryptogram(), getExpirationDate(), getAccount());
  }

  @Override
  public String toString() {
    return "AccountCredit{" +
      "id=" + id +
      ", amount=" + amount +
      ", description='" + description + '\'' +
      ", creditCardNumber='" + creditCardNumber + '\'' +
      ", cryptogram='" + cryptogram + '\'' +
      ", expirationDate='" + expirationDate + '\'' +
      ", account=" + account +
      '}';
  }
}
