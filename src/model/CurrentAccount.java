package model;

public class CurrentAccount extends Account {
    private static final double INTEREST_RATE = 2.0;

    public CurrentAccount(int accountId, String accountNumber, double balance) {
        super(accountId, accountNumber, balance, "Current");
    }

    @Override
    public double getInterestRate() {
        return INTEREST_RATE;
    }
}
