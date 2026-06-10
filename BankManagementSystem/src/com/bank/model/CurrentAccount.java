package com.bank.model;

/**
 * Current account designed for businesses — higher limits, overdraft facility,
 * and no interest accrual.
 */
public class CurrentAccount extends Account {

    private static final long serialVersionUID = 1L;

    public static final double MIN_BALANCE      = 5000.0;
    public static final double WITHDRAWAL_LIMIT = 2_00_000.0;

    private double overdraftLimit;

    public CurrentAccount(String accountNumber, String holderName,
                          double initialBalance, String pin) {
        super(accountNumber, holderName, initialBalance, pin);
        this.overdraftLimit = 10_000.0;   // default ₹10 000 overdraft
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override public String getAccountType()    { return "Current"; }
    @Override public double getWithdrawalLimit() { return WITHDRAWAL_LIMIT; }

    /**
     * Minimum balance for a current account takes the overdraft into account —
     * the effective floor is {@code MIN_BALANCE - overdraftLimit}.
     */
    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE - overdraftLimit;
    }

    // -------------------------------------------------------------------------
    // Overdraft
    // -------------------------------------------------------------------------

    public double getOverdraftLimit() { return overdraftLimit; }

    public void setOverdraftLimit(double limit) {
        if (limit < 0) throw new IllegalArgumentException("Overdraft limit cannot be negative.");
        this.overdraftLimit = limit;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Overdraft Limit: ₹%.2f", overdraftLimit);
    }
}
