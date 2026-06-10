package com.bank.exception;

/** Thrown when the PIN supplied by the user does not match the stored hash. */
public class InvalidPinException extends Exception {
    public InvalidPinException() {
        super("Invalid PIN. Please try again.");
    }
}
