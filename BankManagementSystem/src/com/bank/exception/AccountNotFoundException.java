package com.bank.exception;

/** Thrown when an operation is attempted on an account that does not exist. */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
