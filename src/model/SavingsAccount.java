package model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 4.5;

    public SavingsAccount(int accountId, String accountNumber, double balance) {
        super(accountId, accountNumber, balance, "Savings");
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}
