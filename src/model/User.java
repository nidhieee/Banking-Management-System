package model;

public class User {
    public int accountNo;
    public String fullName;
    public String pin;
    public double balance;
    public String email;

    public User(int accountNo, String fullName, String pin, double balance, String email) {
        this.accountNo = accountNo;
        this.fullName = fullName;
        this.pin = pin;
        this.balance = balance;
        this.email = email;
    }
}
