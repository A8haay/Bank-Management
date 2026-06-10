package com.bank.model;

/**
 * Enumerates every kind of transaction the system can record.
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT,
    INTEREST,
    ACCOUNT_OPENED,
    ACCOUNT_CLOSED
}
