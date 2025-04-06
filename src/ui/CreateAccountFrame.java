package ui;

import db.DatabaseConnection;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class CreateAccountFrame extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField pinField;

    public CreateAccountFrame() {
        setTitle("Create New Account");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Crescent Bank");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 30, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Full Name
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // Email
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        emailField = new JTextField(15);
        panel.add(emailField, gbc);

        // PIN
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("PIN:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        pinField = new JPasswordField(15);
        panel.add(pinField, gbc);

        // Create Button
        JButton createButton = new JButton("Create Account");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(createButton, gbc);

        // Back to Login Button
        JButton backButton = new JButton("Back to Login");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(Color.BLUE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy++;
        panel.add(backButton, gbc);

        add(panel);
        setVisible(true);

        // Action: Create Account
        createButton.addActionListener(e -> createAccount());

        // Action: Back
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
    }

    private void createAccount() {
        String name = nameField.getText();
        String email = emailField.getText();
        String pin = new String(pinField.getPassword());

        if (name.isEmpty() || email.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (full_name, email, pin, balance) VALUES (?, ?, ?, 0)",
                Statement.RETURN_GENERATED_KEYS
            );
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, pin);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int accountNo = keys.getInt(1);
                    JOptionPane.showMessageDialog(this, "Account created! Your account number is: " + accountNo);
                    dispose();
                    new LoginFrame();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.");
        }
    }
}
