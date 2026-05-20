package service;

import dao.AccountDAO;
import util.DBConnection;
import java.sql.*;

public class BankService {

    private AccountDAO accountDAO = new AccountDAO();

    public boolean deposit(int accountId, double amount) {
        double current = accountDAO.getBalance(accountId);
        return accountDAO.updateBalance(accountId, current + amount)
                && logTransaction(accountId, "DEPOSIT", amount);
    }

    public boolean withdraw(int accountId, double amount) {
        double current = accountDAO.getBalance(accountId);
        if (current < amount) {
            System.out.println("  ❌ Insufficient balance!");
            return false;
        }
        return accountDAO.updateBalance(accountId, current - amount)
                && logTransaction(accountId, "WITHDRAW", amount);
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        double current = accountDAO.getBalance(fromAccountId);
        if (current < amount) {
            System.out.println("  ❌ Insufficient balance!");
            return false;
        }
        accountDAO.updateBalance(fromAccountId, current - amount);
        double toBalance = accountDAO.getBalance(toAccountId);
        accountDAO.updateBalance(toAccountId, toBalance + amount);
        logTransaction(fromAccountId, "TRANSFER", amount);
        return true;
    }

    private boolean logTransaction(int accountId, String type, double amount) {
        String query = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public void showTransactions(int accountId) {
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n  ID   TYPE        AMOUNT        DATE");
            System.out.println("  ---  ----------  ------------  -------------------");
            while (rs.next()) {
                System.out.printf("  %-4d %-10s  ₹ %-10.2f  %s%n",
                        rs.getInt("transaction_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
