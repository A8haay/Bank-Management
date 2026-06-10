package com.bank.exception;

/** Thrown when a withdrawal or transfer would leave the account below its minimum balance. */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(double available, double requested) {
        super(String.format(
                "Insufficient funds. Requested: ₹%.2f | Available: ₹%.2f",
                requested, available));
    }
}
