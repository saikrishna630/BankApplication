package com.bank;

import java.sql.*;
import java.util.Scanner;

public class BankApplication {
    static final String URL = "jdbc:mysql://localhost:3306/bank_db";
    static final String USER = "root";
    static final String PASSWORD = "sai18";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n====== Bank Menu ======");
            System.out.println("1. Create Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Delete Account");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> createAccount(sc);
                case 2 -> viewAccounts();
                case 3 -> depositMoney(sc);
                case 4 -> withdrawMoney(sc);
                case 5 -> deleteAccount(sc);
                case 6 -> {
                    System.out.println("Goodbye");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void createAccount(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "INSERT INTO bank_account VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Initial Balance: ");
            double balance = sc.nextDouble();

            ps.setInt(1, accNo);
            ps.setString(2, name);
            ps.setDouble(3, balance);

            ps.executeUpdate();
            System.out.println("Account Created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void viewAccounts() {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM bank_account";
            ResultSet rs = con.createStatement().executeQuery(query);

            while (rs.next()) {
                System.out.println("Account No: " + rs.getInt(1) +
                        ", Name: " + rs.getString(2) +
                        ", Balance: ₹" + rs.getDouble(3));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void depositMoney(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            System.out.print("Enter Amount to Deposit: ");
            double amt = sc.nextDouble();

            String query = "UPDATE bank_account SET balance = balance + ? WHERE acc_no = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, amt);
            ps.setInt(2, accNo);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Deposit Successful!");
            } else {
                System.out.println("Account Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void withdrawMoney(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.print("Enter Account Number: ");
            int accNo = sc.nextInt();
            System.out.print("Enter Amount to Withdraw: ");
            double amt = sc.nextDouble();

            // Check balance first
            String checkQuery = "SELECT balance FROM bank_account WHERE acc_no = ?";
            PreparedStatement checkPs = con.prepareStatement(checkQuery);
            checkPs.setInt(1, accNo);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble(1);
                if (currentBalance >= amt) {
                    String updateQuery = "UPDATE bank_account SET balance = balance - ? WHERE acc_no = ?";
                    PreparedStatement ps = con.prepareStatement(updateQuery);
                    ps.setDouble(1, amt);
                    ps.setInt(2, accNo);
                    ps.executeUpdate();
                    System.out.println("Withdrawal Successful!");
                } else {
                    System.out.println("Insufficient Funds.");
                }
            } else {
                System.out.println("Account Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void deleteAccount(Scanner sc) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.print("Enter Account Number to Delete: ");
            int accNo = sc.nextInt();

            String query = "DELETE FROM bank_account WHERE acc_no = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, accNo);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Account Deleted.");
            } else {
                System.out.println("Account Not Found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
