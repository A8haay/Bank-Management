# 🏦 Bank Management System — Java

A console-based Bank Management System built in Java, demonstrating core OOP principles: **inheritance**, **encapsulation**, **abstraction**, and **polymorphism**. Account data persists between runs using Java serialization.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Account Types** | Savings (4 % interest) and Current (overdraft facility) |
| **Create Account** | Name validation, minimum-balance enforcement, PIN hashing (SHA-256) |
| **Deposit / Withdraw** | Per-type withdrawal limits and minimum-balance guard |
| **Fund Transfer** | PIN-authenticated inter-account transfers |
| **Transaction History** | Full timestamped log per account |
| **Change PIN** | Old-PIN verification before update |
| **Close Account** | Soft-delete — record retained for audit |
| **Admin Panel** | View all accounts, apply annual interest to all savings accounts |
| **Persistence** | Data saved to `data/accounts.dat` via Java serialization |

---

## 🗂️ Project Structure

```
BankManagementSystem/
├── src/
│   └── com/bank/
│       ├── Main.java                    ← Entry point
│       ├── model/
│       │   ├── Account.java             ← Abstract base class
│       │   ├── SavingsAccount.java      ← Savings subclass
│       │   ├── CurrentAccount.java      ← Current subclass
│       │   ├── Transaction.java         ← Immutable transaction record
│       │   └── TransactionType.java     ← Enum of transaction types
│       ├── service/
│       │   └── BankService.java         ← All business logic
│       ├── ui/
│       │   └── BankUI.java              ← Console interface
│       ├── util/
│       │   ├── BankUtils.java           ← Helpers (hashing, validation)
│       │   └── FileHandler.java         ← Serialization persistence
│       └── exception/
│           ├── AccountNotFoundException.java
│           ├── InsufficientFundsException.java
│           └── InvalidPinException.java
├── data/                                ← Auto-created; stores accounts.dat
├── docs/                                ← UML / additional documentation
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher (`java -version`)

### Compile

```bash
# from the project root
javac -d out/production src/com/bank/**/*.java
```

> **Windows (cmd/PowerShell):** list files explicitly if glob doesn't expand:
> ```cmd
> javac -d out\production ^
>   src\com\bank\Main.java ^
>   src\com\bank\model\*.java ^
>   src\com\bank\service\*.java ^
>   src\com\bank\ui\*.java ^
>   src\com\bank\util\*.java ^
>   src\com\bank\exception\*.java
> ```

### Run

```bash
java -cp out/production com.bank.Main
```

---

## 🔐 Security Notes

- PINs are **never stored in plain text** — SHA-256 hashed before persistence.
- Account data file (`data/accounts.dat`) is listed in `.gitignore` and should not be committed.

---

## 📐 OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Abstraction** | `Account` is abstract; `getAccountType()`, `getWithdrawalLimit()`, `getMinimumBalance()` are abstract methods |
| **Inheritance** | `SavingsAccount` and `CurrentAccount` extend `Account` |
| **Encapsulation** | All fields private; exposed only through getters/setters |
| **Polymorphism** | `BankService` works with `Account` references — type-specific behaviour resolved at runtime |
| **Exception Handling** | Custom checked exceptions for business-rule violations |

---

## 🛠️ Technologies

- **Language**: Java 11+
- **Persistence**: Java Object Serialization (`ObjectOutputStream` / `ObjectInputStream`)
- **Security**: `java.security.MessageDigest` (SHA-256) for PIN hashing

---

## 📌 Future Enhancements

- [ ] Swing / JavaFX GUI
- [ ] JDBC / SQLite database backend
- [ ] Admin authentication
- [ ] CSV / PDF statement export
- [ ] Multi-user concurrent access with locks

---

## 👤 Author

**Abhay**  
B.Tech ECE — KIET Group of Institutions, Ghaziabad  
[GitHub](https://github.com/) · [LinkedIn](https://linkedin.com/)

---

## 📄 License

This project is open-source under the [MIT License](LICENSE).
