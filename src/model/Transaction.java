package model;

public class Transaction {
    private int transactionId;
    private int accountId;
    private String type;
    private double amount;
    private String date;

    public Transaction(int transactionId, int accountId, String type, double amount, String date) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public int getTransactionId() { return transactionId; }
    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
