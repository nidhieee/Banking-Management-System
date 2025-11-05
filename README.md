# Crescent Bank – Java Swing Application

A desktop-based banking management system built using **Java Swing** and **MySQL**, designed to simulate real-world banking operations such as account management, fund transfers, deposits, and withdrawals. The project demonstrates the integration of Java GUI components with a relational database for secure and efficient data handling.

---

## 1. Overview

The **Crescent Bank System** provides an interactive user interface for both customers and staff to manage basic banking activities.  
It emphasizes usability, modular design, and database-driven operations while maintaining a lightweight architecture.

---

## 2. Features

- **User Authentication:** Secure login using Account Number and PIN.  
- **Account Dashboard:** Displays balance, personal details, and recent transactions.  
- **Deposit and Withdrawal:** Handles fund transactions with instant balance updates.  
- **Fund Transfer:** Allows money transfer between registered accounts.  
- **PIN Management:** Enables PIN change for added account security.  
- **Account Creation:** Supports registration of new user accounts.  
- **Database Integration:** Uses MySQL for persistent data storage.

---

## 3. Technology Stack

| Component | Technology Used |
|------------|----------------|
| Programming Language | Java (JDK 8 or higher) |
| GUI Framework | Java Swing |
| Database | MySQL |
| Connector Library | MySQL Connector/J |
| IDE (Recommended) | IntelliJ IDEA / Eclipse / VS Code |

---

## 4. Project Structure

Banking-Management-System/
│
├── lib/ # External libraries (e.g., MySQL Connector/J)
├── src/ # Source code for application logic
│ ├── DatabaseConnection.java
│ ├── LoginFrame.java
│ ├── DashboardFrame.java
│ ├── TransactionFrame.java
│ └── ... (other UI and logic classes)
│
├── Main.java # Entry point of the application
├── banking_db.sql # Database schema and initial data
└── README.md # Project documentation

## 5. Installation and Setup

### Prerequisites
Ensure the following are installed:
- Java Development Kit (JDK 8 or later)
- MySQL Server and MySQL Workbench
- MySQL Connector/J (place `.jar` file inside the `lib` directory)

### Database Setup
1. Open MySQL Workbench.
2. Create a new schema (database) — for example:  
   ```sql
   CREATE DATABASE banking_db;
   USE banking_db;
3. Import the provided banking_db.sql file to initialize tables and sample data.

### Configure Database Connection

In DatabaseConnection.java, update your MySQL credentials:

String url = "jdbc:mysql://localhost:3306/banking_db";
String user = "root";
String password = "your_password";

## 6. Compilation and Execution

From the project root directory, run the following commands:

javac -cp ".;lib/mysql-connector-java-8.0.xx.jar" src/**/*.java
java -cp ".;lib/mysql-connector-java-8.0.xx.jar;src" Main
