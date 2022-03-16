package com.openclassrooms.paymybuddy.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "transaction_id")
  private int id;

  @Column(name = "amount")
  @NotNull
  private float amount;

  @Column(name = "description")
  @Size(max = 200)
  private String description;

  @ManyToOne(
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "from_account")
  @NotNull
  private Account fromAccount;

  @ManyToOne(
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "to_account")
  @NotNull
  private Account toAccount;

  public Transaction() {

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

  public Account getFromAccount() {
    return fromAccount;
  }

  public void setFromAccount(Account fromAccount) {
    this.fromAccount = fromAccount;
  }

  public Account getToAccount() {
    return toAccount;
  }

  public void setToAccount(Account toAccount) {
    this.toAccount = toAccount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Transaction that = (Transaction) o;
    return getId() == that.getId() && Float.compare(that.getAmount(), getAmount()) == 0 && Objects.equals(getDescription(), that.getDescription()) && getFromAccount().equals(that.getFromAccount()) && getToAccount().equals(that.getToAccount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getAmount(), getDescription(), getFromAccount(), getToAccount());
  }

  @Override
  public String toString() {
    return "Transaction{" +
      "id=" + id +
      ", amount=" + amount +
      ", fromAccount=" + fromAccount.getUser().getEmail() +
      ", toAccount=" + toAccount.getUser().getEmail() +
      '}';
  }
}
