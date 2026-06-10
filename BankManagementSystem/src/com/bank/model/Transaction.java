package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record of a single banking operation.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final String          transactionId;
    private final TransactionType type;
    private final double          amount;
    private final double          balanceAfter;
    private final String          description;
    private final LocalDateTime   timestamp;

    private static int counter = 1;

    public Transaction(TransactionType type, double amount,
                       double balanceAfter, String description) {
        this.transactionId = "TXN" + String.format("%06d", counter++);
        this.type          = type;
        this.amount        = amount;
        this.balanceAfter  = balanceAfter;
        this.description   = description;
        this.timestamp     = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String          getTransactionId() { return transactionId; }
    public TransactionType getType()          { return type; }
    public double          getAmount()        { return amount; }
    public double          getBalanceAfter()  { return balanceAfter; }
    public String          getDescription()   { return description; }
    public LocalDateTime   getTimestamp()     { return timestamp; }

    @Override
    public String toString() {
        return String.format(
                "[%s] %-12s | %-10s | Amount: ₹%10.2f | Balance: ₹%10.2f | %s",
                timestamp.format(FMT),
                transactionId,
                type,
                amount,
                balanceAfter,
                description);
    }
}
