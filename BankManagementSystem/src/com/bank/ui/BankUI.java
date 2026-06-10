package com.bank.ui;

import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InvalidPinException;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.service.BankService;
import com.bank.util.BankUtils;

import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the Bank Management System.
 * All user interaction lives here; business logic is delegated to {@link BankService}.
 */
public class BankUI {

    private final BankService bankService;
    private final Scanner     scanner;

    public BankUI() {
        this.bankService = new BankService();
        this.scanner     = new Scanner(System.in);
    }

    // =========================================================================
    // Entry point
    // =========================================================================

    public void start() {
        printWelcome();
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1  -> createAccount();
                case 2  -> deposit();
                case 3  -> withdraw();
                case 4  -> transfer();
                case 5  -> checkBalance();
                case 6  -> viewTransactionHistory();
                case 7  -> viewAccountDetails();
                case 8  -> changePin();
                case 9  -> closeAccount();
                case 10 -> adminPanel();
                case 0  -> running = false;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        System.out.println("\nThank you for banking with us. Goodbye!");
        scanner.close();
    }

    // =========================================================================
    // Menu screens
    // =========================================================================

    private void printWelcome() {
        BankUtils.printSeparator();
        System.out.println("   WELCOME TO JAVA BANK MANAGEMENT SYSTEM");
        BankUtils.printSeparator();
    }

    private void printMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println(" 1. Create New Account");
        System.out.println(" 2. Deposit Money");
        System.out.println(" 3. Withdraw Money");
        System.out.println(" 4. Transfer Money");
        System.out.println(" 5. Check Balance");
        System.out.println(" 6. Transaction History");
        System.out.println(" 7. Account Details");
        System.out.println(" 8. Change PIN");
        System.out.println(" 9. Close Account");
        System.out.println("10. Admin Panel");
        System.out.println(" 0. Exit");
        System.out.println("================================");
    }

    // =========================================================================
    // Operations
    // =========================================================================

    private void createAccount() {
        System.out.println("\n--- Create New Account ---");
        String name = readString("Enter full name: ");
        System.out.println("Account type: 1. Savings  2. Current");
        int typeChoice    = readInt("Choice: ");
        String type       = (typeChoice == 2) ? "current" : "savings";
        double deposit    = readDouble("Initial deposit amount: ₹");
        String pin        = readPin("Set 4-digit PIN: ");
        String confirmPin = readPin("Confirm PIN: ");

        if (!pin.equals(confirmPin)) {
            System.out.println("PINs do not match. Account not created.");
            return;
        }

        try {
            Account account = bankService.createAccount(name, deposit, pin, type);
            BankUtils.printSeparator();
            System.out.println("Account created successfully!");
            System.out.println("Account Number : " + account.getAccountNumber());
            System.out.println("Account Type   : " + account.getAccountType());
            System.out.println("Opening Balance: " + BankUtils.formatCurrency(account.getBalance()));
            System.out.println("Please note your account number for future transactions.");
            BankUtils.printSeparator();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deposit() {
        System.out.println("\n--- Deposit Money ---");
        String accountNumber = readString("Account number: ");
        double amount        = readDouble("Deposit amount: ₹");

        try {
            bankService.deposit(accountNumber, amount);
            System.out.println("Deposit of " + BankUtils.formatCurrency(amount) + " successful.");
        } catch (AccountNotFoundException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n--- Withdraw Money ---");
        String accountNumber = readString("Account number: ");
        String pin           = readPin("PIN: ");
        double amount        = readDouble("Withdrawal amount: ₹");

        try {
            bankService.withdraw(accountNumber, amount, pin);
            System.out.println("Withdrawal of " + BankUtils.formatCurrency(amount) + " successful.");
        } catch (AccountNotFoundException | InvalidPinException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("\n--- Transfer Money ---");
        String from   = readString("Your account number: ");
        String pin    = readPin("Your PIN: ");
        String to     = readString("Beneficiary account number: ");
        double amount = readDouble("Transfer amount: ₹");

        try {
            bankService.transfer(from, to, amount, pin);
            System.out.println("Transfer of " + BankUtils.formatCurrency(amount) + " to " + to + " successful.");
        } catch (AccountNotFoundException | InvalidPinException |
                 IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkBalance() {
        System.out.println("\n--- Balance Enquiry ---");
        String accountNumber = readString("Account number: ");
        String pin           = readPin("PIN: ");

        try {
            double balance = bankService.checkBalance(accountNumber, pin);
            System.out.println("Current Balance: " + BankUtils.formatCurrency(balance));
        } catch (AccountNotFoundException | InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        String accountNumber = readString("Account number: ");
        String pin           = readPin("PIN: ");

        try {
            List<Transaction> history = bankService.getTransactionHistory(accountNumber, pin);
            if (history.isEmpty()) {
                System.out.println("No transactions found.");
                return;
            }
            BankUtils.printSeparator();
            history.forEach(System.out::println);
            BankUtils.printSeparator();
        } catch (AccountNotFoundException | InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAccountDetails() {
        System.out.println("\n--- Account Details ---");
        String accountNumber = readString("Account number: ");
        String pin           = readPin("PIN: ");

        try {
            Account account = bankService.getAccountDetails(accountNumber, pin);
            BankUtils.printSeparator();
            System.out.println("Account Number : " + account.getAccountNumber());
            System.out.println("Account Holder : " + account.getAccountHolderName());
            System.out.println("Account Type   : " + account.getAccountType());
            System.out.println("Balance        : " + BankUtils.formatCurrency(account.getBalance()));
            System.out.println("Status         : " + (account.isActive() ? "Active" : "Inactive"));
            System.out.println("Opened On      : " + account.getCreatedAt().toLocalDate());
            BankUtils.printSeparator();
        } catch (AccountNotFoundException | InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changePin() {
        System.out.println("\n--- Change PIN ---");
        String accountNumber = readString("Account number: ");
        String oldPin        = readPin("Current PIN: ");
        String newPin        = readPin("New 4-digit PIN: ");
        String confirmPin    = readPin("Confirm new PIN: ");

        if (!newPin.equals(confirmPin)) {
            System.out.println("PINs do not match. No changes made.");
            return;
        }

        try {
            bankService.changePin(accountNumber, oldPin, newPin);
            System.out.println("PIN changed successfully.");
        } catch (AccountNotFoundException | InvalidPinException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void closeAccount() {
        System.out.println("\n--- Close Account ---");
        String accountNumber = readString("Account number: ");
        String pin           = readPin("PIN: ");
        System.out.print("Are you sure you want to close this account? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Account closure cancelled.");
            return;
        }

        try {
            bankService.closeAccount(accountNumber, pin);
            System.out.println("Account " + accountNumber + " has been closed.");
        } catch (AccountNotFoundException | InvalidPinException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminPanel() {
        System.out.println("\n--- Admin Panel ---");
        System.out.println("1. View all accounts");
        System.out.println("2. Apply interest to all savings accounts");
        System.out.println("0. Back");
        int choice = readInt("Choice: ");

        switch (choice) {
            case 1 -> {
                List<Account> all = bankService.getAllAccounts();
                if (all.isEmpty()) { System.out.println("No accounts found."); break; }
                BankUtils.printSeparator();
                all.forEach(System.out::println);
                BankUtils.printSeparator();
                System.out.println("Total accounts: " + all.size());
            }
            case 2 -> {
                int count = bankService.applyInterestToAllSavings();
                System.out.println("Interest applied to " + count + " savings account(s).");
            }
            case 0 -> {}
            default -> System.out.println("Invalid choice.");
        }
    }

    // =========================================================================
    // Input helpers
    // =========================================================================

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Defaulting to 0.");
            return 0;
        }
    }

    /** Reads a PIN without echoing (uses Console if available, falls back to Scanner). */
    private String readPin(String prompt) {
        java.io.Console console = System.console();
        if (console != null) {
            char[] pinChars = console.readPassword(prompt);
            return new String(pinChars);
        }
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
