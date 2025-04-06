package ui;

import db.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import model.User;

public class LoginFrame extends JFrame {

    private JTextField accountField;
    private JPasswordField pinField;

    public LoginFrame() {
        setTitle("Crescent Bank - Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ✅ Create a panel that paints the background image
        BackgroundPanel bgPanel = new BackgroundPanel("src/assets/background.jpg"); // <- use your actual path
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false); // transparent so background is visible

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Crescent Bank");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        loginPanel.add(new JLabel("Account No:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        accountField = new JTextField(15);
        loginPanel.add(accountField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        loginPanel.add(new JLabel("PIN:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        pinField = new JPasswordField(15);
        loginPanel.add(pinField, gbc);

        JButton loginBtn = new JButton("Login");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginBtn, gbc);

        JButton createBtn = new JButton("Create New Account");
        createBtn.setBorderPainted(false);
        createBtn.setContentAreaFilled(false);
        createBtn.setForeground(Color.BLUE);
        createBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy++;
        loginPanel.add(createBtn, gbc);

        bgPanel.add(loginPanel, BorderLayout.CENTER);
        add(bgPanel);
        setVisible(true);

        loginBtn.addActionListener(e -> login());
        createBtn.addActionListener(e -> {
            dispose();
            new CreateAccountFrame();
        });
    }

    private void login() {
        String accountStr = accountField.getText();
        String pin = new String(pinField.getPassword());

        if (accountStr.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter account number and PIN.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE account_no = ? AND pin = ?");
            stmt.setInt(1, Integer.parseInt(accountStr));
            stmt.setString(2, pin);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("account_no"),
                    rs.getString("full_name"),
                    rs.getString("pin"),
                    rs.getDouble("balance"),
                    rs.getString("email")
                );
                dispose();
                new DashboardFrame(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account number or PIN.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed. Please try again.");
        }
    }
}