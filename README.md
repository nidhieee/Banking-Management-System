# Crescent Bank - Java Swing Project

### 💾 Requirements:
- Java JDK 8+
- MySQL Workbench
- MySQL Connector/J (drop the .jar into `lib` folder)

### 🏦 Setup:

1. Import the `banking_db.sql` into your MySQL server.
2. Replace DB credentials in `DatabaseConnection.java` if needed.
3. Open terminal and run:

```bash
javac -cp ".;lib/mysql-connector-java-8.0.xx.jar" src/**/*.java
java -cp ".;lib/mysql-connector-java-8.0.xx.jar;src" Main
```

### ✅ Features:
- Login with Account No + PIN
- Dashboard: balance, transactions, personal info
- Deposit / Withdraw / Transfer
- Change PIN
- Create new account
