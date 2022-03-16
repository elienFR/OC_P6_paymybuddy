package com.openclassrooms.paymybuddy.model;

import com.openclassrooms.paymybuddy.model.utils.CurrencyCode;
import com.sun.istack.NotNull;
import org.checkerframework.checker.units.qual.Length;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency_iso")
  @NotNull
  private CurrencyCode currencyCode;

  @Column(name = "balance")
  @NotNull
  private float balance;

  @OneToOne(
    cascade = CascadeType.REFRESH,
    fetch = FetchType.LAZY
  )
  @JoinColumn(name = "user_id")
  @NotNull
  private User user;

  @OneToMany(
    mappedBy = "fromAccount",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<Transaction> transactionsFromThisAccount = new ArrayList<>();

  @OneToMany(
    mappedBy = "toAccount",
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<Transaction> transactionsToThisAccount = new ArrayList<>();

  @OneToMany(
    mappedBy = "account",
    cascade = CascadeType.ALL,
    orphanRemoval = true,
    fetch = FetchType.LAZY
  )
  private List<BankTransaction> bankTransactions = new ArrayList<>();

  public Account() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public CurrencyCode getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(CurrencyCode currencyCode) {
    this.currencyCode = currencyCode;
  }

  public float getBalance() {
    return balance;
  }

  public void setBalance(float balance) {
    this.balance = balance;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Transaction> getTransactionsFromThisAccount() {
    return transactionsFromThisAccount;
  }

  public List<Transaction> getTransactionsToThisAccount() {
    return transactionsToThisAccount;
  }

  public List<BankTransaction> getBankTransactions() {
    return bankTransactions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return getId() == account.getId() && Float.compare(account.getBalance(), getBalance()) == 0 && getCurrencyCode() == account.getCurrencyCode() && getUser().equals(account.getUser()) && Objects.equals(getTransactionsFromThisAccount(), account.getTransactionsFromThisAccount()) && Objects.equals(getTransactionsToThisAccount(), account.getTransactionsToThisAccount()) && Objects.equals(getBankTransactions(), account.getBankTransactions());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getCurrencyCode(), getBalance(), getUser(), getTransactionsFromThisAccount(), getTransactionsToThisAccount(), getBankTransactions());
  }

  @Override
  public String toString() {
    return "Account{" +
      "id=" + id +
      ", currencyCode=" + currencyCode +
      ", balance=" + balance +
      ", user=" + user +
      '}';
  }

  public void addTransactionFromThisAccount(Transaction transaction) {
    transactionsFromThisAccount.add(transaction);
    transaction.setFromAccount(this);
    this.balance = this.balance - transaction.getAmount();
  }

  public void addTransactionToThisAccount(Transaction transaction) {
    transactionsToThisAccount.add(transaction);
    transaction.setToAccount(this);
    this.balance = this.balance + transaction.getAmount();
  }
}
