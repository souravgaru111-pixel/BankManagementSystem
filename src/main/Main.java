package main;

import dao.AccountDAO;
import dao.UserDAO;
import model.User;
import service.BankService;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static UserDAO userDAO = new UserDAO();
    static AccountDAO accountDAO = new AccountDAO();
    static BankService bankService = new BankService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("      WELCOME TO BANK MANAGEMENT SYSTEM         ");
            System.out.println("================================================");
            System.out.println("  [1]  Login");
            System.out.println("  [2]  Register");
            System.out.println("  [3]  Admin Login");
            System.out.println("  [4]  Exit");
            System.out.print("\n  Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) login();
            else if (choice == 2) register();
            else if (choice == 3) adminLogin();
            else { System.out.println("  Goodbye!"); break; }
        }
    }

    static void register() {
        sc.nextLine();
        System.out.print("  Enter Name     : ");
        String name = sc.nextLine();
        if (name.isEmpty()) { System.out.println("  ❌ Name cannot be empty!"); return; }

        System.out.print("  Enter Email    : ");
        String email = sc.nextLine();
        if (!isValidEmail(email)) { System.out.println("  ❌ Invalid email format!"); return; }

        System.out.print("  Enter Password : ");
        String password = sc.nextLine();
        if (!isValidPassword(password)) { System.out.println("  ❌ Password must be at least 6 characters!"); return; }

        System.out.print("  Account Type [1-Savings / 2-Current]: ");
        int type = sc.nextInt();
        if (type != 1 && type != 2) { System.out.println("  ❌ Invalid account type!"); return; }

        System.out.print("  Initial Deposit: ");
        double deposit = sc.nextDouble();
        if (!isValidAmount(deposit)) { System.out.println("  ❌ Invalid deposit amount!"); return; }

        String accountType = (type == 1) ? "Savings" : "Current";
        boolean registered = userDAO.registerUser(name, email, password, "customer");
        if (registered) {
            User user = userDAO.getUserByEmail(email);
            String accNumber = accountType.substring(0,2).toUpperCase() + "-2024-" + String.format("%05d", user.getUserId());
            accountDAO.createAccount(user.getUserId(), accNumber, deposit, accountType);
            System.out.println("\n  ✔ Registration Successful!");
            System.out.println("  ✔ Account Number: " + accNumber);
        }
    }

    static void login() {
        sc.nextLine();
        System.out.print("  Enter Email    : "); String email = sc.nextLine();
        System.out.print("  Enter Password : "); String password = sc.nextLine();

        User user = userDAO.getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("\n  ✔ Welcome, " + user.getName() + "!");
            int accountId = accountDAO.getAccountIdByUserId(user.getUserId());
            dashboard(user, accountId);
        } else {
            System.out.println("\n  ❌ Invalid email or password!");
        }
    }

    static void dashboard(User user, int accountId) {
        while (true) {
            double balance = accountDAO.getBalance(accountId);
            System.out.println("\n================================================");
            System.out.println("  DASHBOARD — " + user.getName());
            System.out.println("  Balance: ₹ " + balance);
            System.out.println("================================================");
            System.out.println("  [1]  Deposit");
            System.out.println("  [2]  Withdraw");
            System.out.println("  [3]  Transfer");
            System.out.println("  [4]  Transaction History");
            System.out.println("  [5]  Logout");
            System.out.print("\n  Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                System.out.print("  Enter amount: "); double amt = sc.nextDouble();
                if (bankService.deposit(accountId, amt))
                    System.out.println("  ✔ ₹" + amt + " deposited!");
            } else if (choice == 2) {
                System.out.print("  Enter amount: "); double amt = sc.nextDouble();
                if (bankService.withdraw(accountId, amt))
                    System.out.println("  ✔ ₹" + amt + " withdrawn!");
            } else if (choice == 3) {
                System.out.print("  Enter receiver account ID: "); int toId = sc.nextInt();
                System.out.print("  Enter amount: "); double amt = sc.nextDouble();
                if (bankService.transfer(accountId, toId, amt))
                    System.out.println("  ✔ ₹" + amt + " transferred!");
            } else if (choice == 4) {
                bankService.showTransactions(accountId);
            } else {
                System.out.println("  Logged out!");
                break;
            }
        }
    }

    static boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    static boolean isValidAmount(double amount) {
        return amount > 0;
    }

    static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
    static void adminLogin() {
        sc.nextLine();
        System.out.print("  Enter Admin Email    : ");
        String email = sc.nextLine();
        System.out.print("  Enter Admin Password : ");
        String password = sc.nextLine();

        if (email.equals("admin@bank.com") && password.equals("admin123")) {
            System.out.println("\n  ✔ Welcome Admin!");
            adminPanel();
        } else {
            System.out.println("\n  ❌ Invalid admin credentials!");
        }
    }

    static void adminPanel() {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("           ADMIN PANEL                          ");
            System.out.println("================================================");
            System.out.println("  [1]  View All Users");
            System.out.println("  [2]  View All Accounts");
            System.out.println("  [3]  Logout");
            System.out.print("\n  Enter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                showAllUsers();
            } else if (choice == 2) {
                showAllAccounts();
            } else {
                System.out.println("  Admin logged out!");
                break;
            }
        }
    }

    static void showAllUsers() {
        String query = "SELECT * FROM users";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(query)) {
            System.out.println("\n  ID   NAME              EMAIL");
            System.out.println("  ---  ----------------  --------------------");
            while (rs.next()) {
                System.out.printf("  %-4d %-16s  %s%n",
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"));
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void showAllAccounts() {
        String query = "SELECT * FROM accounts";
        try (java.sql.Connection conn = util.DBConnection.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(query)) {
            System.out.println("\n  ID   ACCOUNT NO       BALANCE      TYPE");
            System.out.println("  ---  ---------------  -----------  -------");
            while (rs.next()) {
                System.out.printf("  %-4d %-15s  ₹ %-9.2f  %s%n",
                        rs.getInt("account_id"),
                        rs.getString("account_number"),
                        rs.getDouble("balance"),
                        rs.getString("account_type"));
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
