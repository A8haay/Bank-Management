# System Design — Bank Management System

## Class Hierarchy

```
Account  (abstract)
├── SavingsAccount   — 4% interest, ₹50k withdrawal limit
└── CurrentAccount   — overdraft facility, ₹2L withdrawal limit
```

## Layer Architecture

```
┌────────────────────────────────────┐
│         BankUI  (com.bank.ui)      │  ← User interaction only
└──────────────┬─────────────────────┘
               │ calls
┌──────────────▼─────────────────────┐
│      BankService (com.bank.service) │  ← Business logic, validation
└──────────────┬─────────────────────┘
               │ reads / writes
┌──────────────▼─────────────────────┐
│   FileHandler  (com.bank.util)     │  ← Persistence (serialization)
└────────────────────────────────────┘
```

## Key Design Decisions

### Why abstract `Account`?
- Enforces a contract: every concrete account **must** declare its type,
  withdrawal limit, and minimum balance.
- `BankService` can hold `Map<String, Account>` and call polymorphic
  methods without knowing the concrete type.

### Why checked exceptions?
- `AccountNotFoundException`, `InvalidPinException` are business-rule
  violations the caller **must** handle — checked forces acknowledgement.

### Why SHA-256 for PINs?
- Even though the data store is a local file, hashing prevents plain-text
  exposure if the file is accidentally shared. No salt is used here for
  simplicity; a production system would use BCrypt or Argon2.

### Why Java Serialization for persistence?
- Keeps the project dependency-free (no JDBC driver, no external DB).
- Swap `FileHandler` for a JDBC implementation without touching any other
  class — the service layer is fully decoupled from storage.

## Transaction Log Design

Each `Account` owns its `List<Transaction>`.  
A `Transaction` is **immutable** — once created, no field changes.  
`TransactionType` enum makes switch/filter on type safe and readable.
