package com.bank.model;

/**
 * Savings account with interest accrual and conservative withdrawal limits.
 */
public class SavingsAccount extends Account {

    private static final long serialVersionUID = 1L;

    public static final double INTEREST_RATE       = 0.04;   // 4 % per annum
    public static final double MIN_BALANCE         = 1000.0;
    public static final double WITHDRAWAL_LIMIT    = 50_000.0;

    private double interestRate;

    public SavingsAccount(String accountNumber, String holderName,
                          double initialBalance, String pin) {
        super(accountNumber, holderName, initialBalance, pin);
        this.interestRate = INTEREST_RATE;
    }

    // -------------------------------------------------------------------------
    // Abstract method implementations
    // -------------------------------------------------------------------------

    @Override public String getAccountType()   { return "Savings"; }
    @Override public double getWithdrawalLimit() { return WITHDRAWAL_LIMIT; }
    @Override public double getMinimumBalance()  { return MIN_BALANCE; }

    // -------------------------------------------------------------------------
    // Interest
    // -------------------------------------------------------------------------

    /**
     * Applies annual interest to the current balance and records the transaction.
     *
     * @return the interest amount credited
     */
    public double applyInterest() {
        double interest = getBalance() * interestRate;
        setBalance(getBalance() + interest);
        addTransaction(new Transaction(TransactionType.INTEREST, interest, getBalance(),
                "Annual interest credited at " + (interestRate * 100) + "%"));
        return interest;
    }

    public double getInterestRate() { return interestRate; }
    public void   setInterestRate(double rate) { this.interestRate = rate; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Interest Rate: %.1f%%", interestRate * 100);
    }
}
