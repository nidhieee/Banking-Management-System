package ui;

import db.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import model.User;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private JLabel nameLabel, balanceLabel, emailLabel;
    private DefaultTableModel transactionTableModel;

    public DashboardFrame(User user) {
        this.currentUser = user;

        setTitle("Crescent Bank - Dashboard");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel welcomeLabel = new JLabel("Welcome to Crescent Bank", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(20, 40, 80));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(welcomeLabel, BorderLayout.NORTH);

        // Side menu
        JPanel menuPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        menuPanel.setPreferredSize(new Dimension(170, 450));
        menuPanel.setBackground(new Color(240, 248, 255));
        menuPanel.setBorder(new EmptyBorder(20, 15, 20, 15));

        JButton depositBtn = createMenuButton("Deposit");
        JButton withdrawBtn = createMenuButton("Withdraw");
        JButton transferBtn = createMenuButton("Transfer");
        JButton changePinBtn = createMenuButton("Change PIN");
        JButton logoutBtn = createMenuButton("Logout");

        depositBtn.addActionListener(e -> performTransaction("deposit"));
        withdrawBtn.addActionListener(e -> performTransaction("withdraw"));
        transferBtn.addActionListener(e -> transferMoney());
        changePinBtn.addActionListener(e -> changePin());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        menuPanel.add(depositBtn);
        menuPanel.add(withdrawBtn);
        menuPanel.add(transferBtn);
        menuPanel.add(changePinBtn);
        menuPanel.add(logoutBtn);

        add(menuPanel, BorderLayout.WEST);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new CompoundBorder(
                new TitledBorder("Account Info"),
                new EmptyBorder(10, 10, 10, 10)));

        nameLabel = new JLabel("Name: " + currentUser.fullName);
        emailLabel = new JLabel("Email: " + currentUser.email);
        balanceLabel = new JLabel("Balance: ₹" + currentUser.balance);

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameLabel.setFont(infoFont);
        emailLabel.setFont(infoFont);
        balanceLabel.setFont(infoFont);

        infoPanel.add(nameLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(balanceLabel);

        // Transaction Table
        transactionTableModel = new DefaultTableModel(new String[]{"Type", "Amount", "Date"}, 0);
        JTable transactionTable = new JTable(transactionTableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setRowHeight(24);
        transactionTable.setShowGrid(false);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane tableScroll = new JScrollPane(transactionTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Transaction History"));

        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadTransactionHistory();
        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(60, 110, 160), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        return btn;
    }

    private void performTransaction(String type) {
        String input = JOptionPane.showInputDialog(this, "Enter amount:");
        if (input == null || input.isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) throw new NumberFormatException();

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement updateStmt;

                if (type.equals("withdraw") && currentUser.balance < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.");
                    return;
                }

                if (type.equals("deposit")) {
                    updateStmt = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE account_no = ?");
                } else {
                    updateStmt = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE account_no = ?");
                }

                updateStmt.setDouble(1, amount);
                updateStmt.setInt(2, currentUser.accountNo);
                updateStmt.executeUpdate();

                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO transactions (account_no, type, amount) VALUES (?, ?, ?)");
                insertStmt.setInt(1, currentUser.accountNo);
                insertStmt.setString(2, type);
                insertStmt.setDouble(3, amount);
                insertStmt.executeUpdate();

                currentUser.balance = type.equals("deposit") ?
                        currentUser.balance + amount : currentUser.balance - amount;

                balanceLabel.setText("Balance: ₹" + currentUser.balance);
                loadTransactionHistory();

                JOptionPane.showMessageDialog(this, "Transaction successful.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void transferMoney() {
        JTextField toAccountField = new JTextField();
        JTextField amountField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("To Account No:"));
        panel.add(toAccountField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int toAccount = Integer.parseInt(toAccountField.getText());
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0 || currentUser.balance < amount) {
                    JOptionPane.showMessageDialog(this, "Invalid or insufficient amount.");
                    return;
                }

                try (Connection conn = DatabaseConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    PreparedStatement withdraw = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE account_no = ?");
                    withdraw.setDouble(1, amount);
                    withdraw.setInt(2, currentUser.accountNo);
                    withdraw.executeUpdate();

                    PreparedStatement deposit = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE account_no = ?");
                    deposit.setDouble(1, amount);
                    deposit.setInt(2, toAccount);
                    int rowsAffected = deposit.executeUpdate();

                    if (rowsAffected == 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Invalid target account.");
                        return;
                    }

                    PreparedStatement txnOut = conn.prepareStatement("INSERT INTO transactions (account_no, type, amount) VALUES (?, 'transfer-out', ?)");
                    txnOut.setInt(1, currentUser.accountNo);
                    txnOut.setDouble(2, amount);
                    txnOut.executeUpdate();

                    PreparedStatement txnIn = conn.prepareStatement("INSERT INTO transactions (account_no, type, amount) VALUES (?, 'transfer-in', ?)");
                    txnIn.setInt(1, toAccount);
                    txnIn.setDouble(2, amount);
                    txnIn.executeUpdate();

                    conn.commit();
                    currentUser.balance -= amount;
                    balanceLabel.setText("Balance: ₹" + currentUser.balance);
                    loadTransactionHistory();
                    JOptionPane.showMessageDialog(this, "Transfer successful.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void changePin() {
        JPasswordField pinField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, pinField, "Enter new PIN", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newPin = new String(pinField.getPassword());
            if (newPin.isEmpty()) return;

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("UPDATE users SET pin = ? WHERE account_no = ?");
                stmt.setString(1, newPin);
                stmt.setInt(2, currentUser.accountNo);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "PIN changed successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadTransactionHistory() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT type, amount, date_time FROM transactions WHERE account_no = ? ORDER BY date_time DESC");
            stmt.setInt(1, currentUser.accountNo);
            ResultSet rs = stmt.executeQuery();

            transactionTableModel.setRowCount(0);
            while (rs.next()) {
                transactionTableModel.addRow(new Object[]{
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("date_time")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
