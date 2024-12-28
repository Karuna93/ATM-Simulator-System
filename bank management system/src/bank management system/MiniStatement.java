package bank.management.system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class MiniStatement extends JFrame implements ActionListener {

    JButton exitButton;
    JLabel bankNameLabel, cardNumberLabel, balanceLabel;
    JTextArea transactionsArea; // To display transactions in a neat, scrollable manner
    JScrollPane transactionsScrollPane; // To make the transactions area scrollable
    String pin;

    MiniStatement(String pin) {
        super("Mini Statement");
        this.pin = pin;

        // Frame settings
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setSize(400, 600);
        setLocation(20, 20);

        // Bank name label
        bankNameLabel = new JLabel("Indian Bank");
        bankNameLabel.setBounds(150, 20, 200, 20);
        bankNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(bankNameLabel);

        // Card number label
        cardNumberLabel = new JLabel();
        cardNumberLabel.setBounds(20, 80, 360, 20);
        cardNumberLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        add(cardNumberLabel);

        // Transactions label (Text area for structured transactions)
        transactionsArea = new JTextArea();
        transactionsArea.setBounds(20, 140, 360, 300);
        transactionsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionsArea.setEditable(false); // Prevent user from editing the transactions
        transactionsArea.setLineWrap(true);
        transactionsArea.setWrapStyleWord(true);
        
        // Adding a JScrollPane for the transactions
        transactionsScrollPane = new JScrollPane(transactionsArea);
        transactionsScrollPane.setBounds(20, 140, 360, 300);
        add(transactionsScrollPane);

        // Balance label
        balanceLabel = new JLabel();
        balanceLabel.setBounds(20, 450, 360, 20);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(balanceLabel);

        // Exit button
        exitButton = new JButton("Exit");
        exitButton.setBounds(150, 500, 100, 30);
        exitButton.addActionListener(this);
        add(exitButton);

        // Fetch and display data
        fetchCardDetails();
        fetchTransactions();

        setVisible(true);
    }

    private void fetchCardDetails() {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM login WHERE pin = ?";
            PreparedStatement stmt = c.c.prepareStatement(query);
            stmt.setString(1, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String cardnumber = rs.getString("cardnumber");
                cardNumberLabel.setText("Card Number: " + cardnumber.substring(0, 4) + "XXXXXXXX" + cardnumber.substring(12));
            } else {
                cardNumberLabel.setText("Card Number: Not Found");
            }
        } catch (Exception e) {
            cardNumberLabel.setText("Error fetching card details.");
            e.printStackTrace();
        }
    }

    private void fetchTransactions() {
        try {
            Conn c = new Conn();
            String query = "SELECT * FROM bank WHERE pin = ?";
            PreparedStatement stmt = c.c.prepareStatement(query);
            stmt.setString(1, pin);
            ResultSet rs = stmt.executeQuery();

            StringBuilder transactionDetails = new StringBuilder();
            int balance = 0;

            while (rs.next()) {
                String date = rs.getString("date");
                String type = rs.getString("type");
                String amountStr = rs.getString("amount");
                int amount = 0;

                // Ensure the amount is a valid integer
                try {
                    amount = Integer.parseInt(amountStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount: " + amountStr);
                    continue; // Skip invalid amounts
                }

                // Add spacing between transactions
                transactionDetails.append(date)
                        .append("\n")
                        .append(type)
                        .append(" Rs ")
                        .append(amount)
                        .append("\n\n"); // Add extra space between transactions

                // Adjust balance based on transaction mode
                if ("Deposit".equalsIgnoreCase(type)) {
                    balance += amount;
                } else if ("Withdrawal".equalsIgnoreCase(type)) {
                    balance -= amount;
                }
            }

            transactionsArea.setText(transactionDetails.toString()); // Set transactions in the text area
            balanceLabel.setText("Your total Balance is Rs " + balance);
        } catch (Exception e) {
            transactionsArea.setText("Error fetching transactions.");
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == exitButton) {
            this.setVisible(false);
        }
    }

    public static void main(String[] args) {
        new MiniStatement("1234").setVisible(true); // Replace with an actual pin
    }
}
