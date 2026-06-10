package com.bank.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Stateless utility helpers shared across the application.
 */
public final class BankUtils {

    private BankUtils() {}   // prevent instantiation

    // -------------------------------------------------------------------------
    // Account number generation
    // -------------------------------------------------------------------------

    private static int accountCounter = 1000;

    /**
     * Generates a unique 12-digit account number of the form {@code YYYYMMDDNNNN}.
     */
    public static synchronized String generateAccountNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return datePart + String.format("%04d", accountCounter++);
    }

    // -------------------------------------------------------------------------
    // PIN hashing (SHA-256)
    // -------------------------------------------------------------------------

    /**
     * Returns the SHA-256 hex digest of the given plain-text PIN.
     *
     * @param pin plain-text 4-digit PIN
     * @return 64-character hex string, or the original on hash failure
     */
    public static String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // Should never happen on any standard JVM
            return pin;
        }
    }

    /**
     * Validates a plain-text PIN against a stored hash.
     */
    public static boolean verifyPin(String plainPin, String storedHash) {
        return hashPin(plainPin).equals(storedHash);
    }

    // -------------------------------------------------------------------------
    // Input validation
    // -------------------------------------------------------------------------

    /** Returns {@code true} if the string is exactly 4 digits. */
    public static boolean isValidPin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    /** Returns {@code true} if the name contains only letters, spaces, and hyphens. */
    public static boolean isValidName(String name) {
        return name != null && !name.isBlank() && name.matches("[a-zA-Z \\-]+");
    }

    /** Formats a double as an Indian rupee string. */
    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }

    /** Prints a separator line of 60 dashes to System.out. */
    public static void printSeparator() {
        System.out.println("-".repeat(60));
    }
}
