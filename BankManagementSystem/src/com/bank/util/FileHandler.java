package com.bank.util;

import com.bank.model.Account;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple file-based persistence using Java serialization.
 * Accounts are stored as a serialised {@code Map<String, Account>}
 * at {@code data/accounts.dat}.
 */
public final class FileHandler {

    private FileHandler() {}

    private static final String DATA_DIR  = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "accounts.dat";

    // -------------------------------------------------------------------------
    // Save
    // -------------------------------------------------------------------------

    /**
     * Serialises the accounts map to disk, creating the data directory if absent.
     *
     * @param accounts map of account-number → Account
     * @return {@code true} on success
     */
    public static boolean saveAccounts(Map<String, Account> accounts) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(accounts);
            return true;
        } catch (IOException e) {
            System.err.println("[FileHandler] Error saving accounts: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Load
    // -------------------------------------------------------------------------

    /**
     * Deserialises the accounts map from disk.
     *
     * @return populated map, or an empty map if the file does not yet exist
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Account> loadAccounts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileHandler] Error loading accounts: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
