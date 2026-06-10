package com.bank.service;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InvalidPinException;
import com.bank.model.*;
import com.bank.util.BankUtils;
import com.bank.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central service layer that owns all banking operations.
 * The UI layer calls only this class — it never touches Account objects directly.
 */
public class BankService {

    private final Map<String, Account> accounts;

    public BankService() {
        this.accounts = FileHandler.loadAccounts();
    }

    // =========================================================================
    // Account management
    // =========================================================================

    /**
     * Opens a new account and persists the change.
     *
     * @param holderName     full name of the account holder
     * @param initialDeposit opening balance (must be ≥ account-type minimum)
     * @param pin            4-digit PIN (plain text; will be hashed before storage)
     * @param type           "savings" or "current" (case-insensitive)
     * @return the newly created {@link Account}
     */
    public Account createAccount(String holderName, double initialDeposit,
                                 String pin, String type) {
        if (!BankUtils.isValidName(holderName)) {
            throw new IllegalArgumentException("Invalid account holder name.");
        }
        if (!BankUtils.isValidPin(pin)) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }

        String accountNumber = BankUtils.generateAccountNumber();
        String hashedPin     = BankUtils.hashPin(pin);
        Account account;

        if ("current".equalsIgnoreCase(type)) {
            if (initialDeposit < CurrentAccount.MIN_BALANCE) {
                throw new IllegalArgumentException(
                        "Minimum opening deposit for a Current account is "
                                + BankUtils.formatCurrency(CurrentAccount.MIN_BALANCE));
            }
            account = new CurrentAccount(accountNumber, holderName, initialDeposit, hashedPin);
        } else {
            if (initialDeposit < SavingsAccount.MIN_BALANCE) {
                throw new IllegalArgumentException(
                        "Minimum opening deposit for a Savings account is "
                                + BankUtils.formatCurrency(SavingsAccount.MIN_BALANCE));
            }
            account = new SavingsAccount(accountNumber, holderName, initialDeposit, hashedPin);
        }

        account.addTransaction(new Transaction(TransactionType.ACCOUNT_OPENED,
                initialDeposit, initialDeposit, "Account opened"));

        accounts.put(accountNumber, account);
        save();
        return account;
    }

    /**
     * Closes an account after PIN verification.
     * The account is marked inactive; its record is retained for audit purposes.
     */
    public void closeAccount(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        Account account = getVerifiedAccount(accountNumber, pin);
        account.setActive(false);
        account.addTransaction(new Transaction(TransactionType.ACCOUNT_CLOSED,
                0, account.getBalance(), "Account closed"));
        save();
    }

    // =========================================================================
    // Transactions
    // =========================================================================

    /** Deposits money into an account (no PIN required — mirrors ATM cash-in). */
    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.deposit(amount);
        save();
    }

    /** Withdraws money after PIN verification. */
    public void withdraw(String accountNumber, double amount, String pin)
            throws AccountNotFoundException, InvalidPinException {
        Account account = getVerifiedAccount(accountNumber, pin);
        account.withdraw(amount);
        save();
    }

    /**
     * Transfers {@code amount} from {@code fromAccount} to {@code toAccount}.
     *
     * @param fromAccountNumber source account number
     * @param toAccountNumber   destination account number
     * @param amount            amount to transfer (positive)
     * @param pin               PIN of the source account
     */
    public void transfer(String fromAccountNumber, String toAccountNumber,
                         double amount, String pin)
            throws AccountNotFoundException, InvalidPinException {
        Account from = getVerifiedAccount(fromAccountNumber, pin);
        Account to   = getAccount(toAccountNumber);

        if (!to.isActive()) {
            throw new IllegalStateException("Destination account is inactive.");
        }

        from.withdraw(amount);
        // Override the last withdrawal transaction's type to TRANSFER_OUT
        List<Transaction> history = from.getTransactionHistory();
        // We add a proper transfer-out entry manually
        from.addTransaction(new Transaction(TransactionType.TRANSFER_OUT, amount,
                from.getBalance(), "Transfer to " + toAccountNumber));

        to.deposit(amount);
        to.addTransaction(new Transaction(TransactionType.TRANSFER_IN, amount,
                to.getBalance(), "Transfer from " + fromAccountNumber));

        save();
    }

    // =========================================================================
    // Queries
    // =========================================================================

    public double checkBalance(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        return getVerifiedAccount(accountNumber, pin).getBalance();
    }

    public Account getAccountDetails(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        return getVerifiedAccount(accountNumber, pin);
    }

    public List<Transaction> getTransactionHistory(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        return getVerifiedAccount(accountNumber, pin).getTransactionHistory();
    }

    /** Returns a snapshot of all accounts (for admin view). */
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    // =========================================================================
    // PIN management
    // =========================================================================

    public void changePin(String accountNumber, String oldPin, String newPin)
            throws AccountNotFoundException, InvalidPinException {
        Account account = getVerifiedAccount(accountNumber, oldPin);
        if (!BankUtils.isValidPin(newPin)) {
            throw new IllegalArgumentException("New PIN must be exactly 4 digits.");
        }
        account.setPin(BankUtils.hashPin(newPin));
        save();
    }

    // =========================================================================
    // Interest (Savings accounts only)
    // =========================================================================

    /**
     * Applies annual interest to every active savings account.
     *
     * @return number of accounts credited
     */
    public int applyInterestToAllSavings() {
        int count = 0;
        for (Account acc : accounts.values()) {
            if (acc.isActive() && acc instanceof SavingsAccount) {
                ((SavingsAccount) acc).applyInterest();
                count++;
            }
        }
        save();
        return count;
    }

    // =========================================================================
    // Internal helpers
    // =========================================================================

    private Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) throw new AccountNotFoundException(accountNumber);
        return account;
    }

    private Account getVerifiedAccount(String accountNumber, String pin)
            throws AccountNotFoundException, InvalidPinException {
        Account account = getAccount(accountNumber);
        if (!BankUtils.verifyPin(pin, account.getPin())) {
            throw new InvalidPinException();
        }
        return account;
    }

    private void save() {
        FileHandler.saveAccounts(accounts);
    }
}
