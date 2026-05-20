package model;

public abstract class Account {
    private int accountId;
    private String accountNumber;
    private double balance;
    private String accountType;

    public Account(int accountId, String accountNumber, double balance, String accountType) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public int getAccountId() { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getAccountType() { return accountType; }

    public abstract double getInterestRate();
}
