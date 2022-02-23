package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bank_transaction_id")
  private long id;

  @Column(name = "amount")
  private float amount;

  @Column(name = "description")
  private String description;

  @Column(name = "exterior_iban")
  private String exteriorIban;

  @Column(name = "exterior_bic")
  private String exteriorBic;

  @ManyToOne(
    cascade = CascadeType.ALL,
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "paymybuddy_account")
  private Account account;

  public BankTransaction() {

  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
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

  public String getExteriorIban() {
    return exteriorIban;
  }

  public void setExteriorIban(String exteriorIban) {
    this.exteriorIban = exteriorIban;
  }

  public String getExteriorBic() {
    return exteriorBic;
  }

  public void setExteriorBic(String exteriorBic) {
    this.exteriorBic = exteriorBic;
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
    BankTransaction that = (BankTransaction) o;
    return getId() == that.getId() && Float.compare(that.getAmount(), getAmount()) == 0 && Objects.equals(getDescription(), that.getDescription()) && getExteriorIban().equals(that.getExteriorIban()) && getExteriorBic().equals(that.getExteriorBic()) && getAccount().equals(that.getAccount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getAmount(), getDescription(), getExteriorIban(), getExteriorBic(), getAccount());
  }

  @Override
  public String toString() {
    return "BankTransaction{" +
      "id=" + id +
      ", amount=" + amount +
      ", exteriorIban='" + exteriorIban + '\'' +
      ", exteriorBic='" + exteriorBic + '\'' +
      ", account=" + account +
      '}';
  }
}
