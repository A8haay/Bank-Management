package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a bank account.
 * All account types (Savings, Current) extend this class.
 */
public abstract class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private String pin;         // stored as hashed string
    private LocalDateTime createdAt;
    private boolean isActive;
    private List<Transaction> transactionHistory;

    // Constructor
    public Account(String accountNumber, String accountHolderName, double initialBalance, String pin) {
        this.accountNumber      = accountNumber;
        this.accountHolderName  = accountHolderName;
        this.balance            = initialBalance;
        this.pin                = pin;
        this.createdAt          = LocalDateTime.now();
        this.isActive           = true;
        this.transactionHistory = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Abstract methods — each subclass defines its own account-type behaviour
    // -------------------------------------------------------------------------

    /** Returns the type label shown in menus and receipts (e.g. "Savings"). */
    public abstract String getAccountType();

    /** Maximum single-transaction withdrawal allowed for this account type. */
    public abstract double getWithdrawalLimit();

    /** Minimum balance that must remain after any withdrawal. */
    public abstract double getMinimumBalance();

    // -------------------------------------------------------------------------
    // Core banking operations
    // -------------------------------------------------------------------------

    /**
     * Deposits {@code amount} into the account and records the transaction.
     *
     * @param amount positive value to deposit
     * @throws IllegalArgumentException if amount ≤ 0
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        addTransaction(new Transaction(TransactionType.DEPOSIT, amount, balance,
                "Deposit to " + accountNumber));
    }

    /**
     * Withdraws {@code amount} from the account, enforcing per-type limits.
     *
     * @param amount positive value to withdraw
     * @throws IllegalArgumentException for non-positive or limit-violating amounts
     * @throws IllegalStateException    if the account is inactive
     */
    public void withdraw(double amount) {
        if (!isActive) {
            throw new IllegalStateException("Account is inactive.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > getWithdrawalLimit()) {
            throw new IllegalArgumentException(
                    "Amount exceeds the single-transaction withdrawal limit of ₹" + getWithdrawalLimit());
        }
        if ((balance - amount) < getMinimumBalance()) {
            throw new IllegalArgumentException(
                    "Insufficient funds. Minimum balance of ₹" + getMinimumBalance() + " must be maintained.");
        }
        balance -= amount;
        addTransaction(new Transaction(TransactionType.WITHDRAWAL, amount, balance,
                "Withdrawal from " + accountNumber));
    }

    // -------------------------------------------------------------------------
    // Transaction log
    // -------------------------------------------------------------------------

    protected void addTransaction(Transaction t) {
        transactionHistory.add(t);
    }

    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);   // defensive copy
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public String getAccountNumber()              { return accountNumber; }
    public String getAccountHolderName()          { return accountHolderName; }
    public double getBalance()                    { return balance; }
    public String getPin()                        { return pin; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public boolean isActive()                     { return isActive; }

    public void setAccountHolderName(String name) { this.accountHolderName = name; }
    public void setPin(String pin)                { this.pin = pin; }
    public void setActive(boolean active)         { this.isActive = active; }
    protected void setBalance(double balance)     { this.balance = balance; }

    @Override
    public String toString() {
        return String.format(
                "Account[%s | %s | %s | Balance: ₹%.2f | Active: %s]",
                accountNumber, accountHolderName, getAccountType(), balance, isActive);
    }
}
