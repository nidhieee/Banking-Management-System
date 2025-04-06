CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

CREATE TABLE users (
    account_no INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    pin VARCHAR(10),
    balance DOUBLE DEFAULT 0.0,
    email VARCHAR(100)
);

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_no INT,
    type VARCHAR(20),
    amount DOUBLE,
    date_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_no) REFERENCES users(account_no)
);
