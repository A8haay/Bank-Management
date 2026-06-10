package com.bank;

import com.bank.ui.BankUI;

/**
 * Application entry point.
 *
 * <p>Run from the project root:
 * <pre>
 *   # Compile
 *   javac -d out/production src/com/bank/**&#47;*.java
 *
 *   # Run
 *   java -cp out/production com.bank.Main
 * </pre>
 */
public class Main {
    public static void main(String[] args) {
        new BankUI().start();
    }
}
