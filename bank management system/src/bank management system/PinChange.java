package bank.management.system;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class PinChange extends JFrame implements ActionListener {

    JPasswordField t1, t2;
    JButton b1, b2;
    JLabel l1, l2, l3;
    String pin;

    PinChange(String pin) {
        this.pin = pin;

        // Setting up background image
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/atm.jpg"));
        Image i2 = i1.getImage().getScaledInstance(1000, 1180, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel l4 = new JLabel(i3);
        l4.setBounds(0, 0, 960, 1080);
        add(l4);

        // Labels
        l1 = new JLabel("CHANGE YOUR PIN");
        l1.setFont(new Font("System", Font.BOLD, 16));
        l1.setForeground(Color.WHITE);

        l2 = new JLabel("New PIN:");
        l2.setFont(new Font("System", Font.BOLD, 16));
        l2.setForeground(Color.WHITE);

        l3 = new JLabel("Re-Enter New PIN:");
        l3.setFont(new Font("System", Font.BOLD, 16));
        l3.setForeground(Color.WHITE);

        // Input fields
        t1 = new JPasswordField();
        t1.setFont(new Font("Raleway", Font.BOLD, 25));

        t2 = new JPasswordField();
        t2.setFont(new Font("Raleway", Font.BOLD, 25));

        // Buttons
        b1 = new JButton("CHANGE");
        b2 = new JButton("BACK");

        b1.addActionListener(this);
        b2.addActionListener(this);

        setLayout(null);

        // Adding components
        l1.setBounds(280, 330, 800, 35);
        l4.add(l1);

        l2.setBounds(180, 390, 150, 35);
        l4.add(l2);

        l3.setBounds(180, 440, 200, 35);
        l4.add(l3);

        t1.setBounds(350, 390, 180, 25);
        l4.add(t1);

        t2.setBounds(350, 440, 180, 25);
        l4.add(t2);

        b1.setBounds(390, 588, 150, 35);
        l4.add(b1);

        b2.setBounds(390, 633, 150, 35);
        l4.add(b2);

        // Frame settings
        setSize(960, 1080);
        setLocation(500, 0);
        setUndecorated(true);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            char[] npinArray = t1.getPassword();
            char[] rpinArray = t2.getPassword();
            String npin = new String(npinArray);
            String rpin = new String(rpinArray);

            // Check if PINs match
            if (!npin.equals(rpin)) {
                JOptionPane.showMessageDialog(null, "Entered PIN does not match");
                return;
            }

            if (ae.getSource() == b1) {
                if (npin.isEmpty() || rpin.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all the fields");
                    return;
                }

                // Database operations
                Conn conn = new Conn();
                int updatedTables = 0;

                String query1 = "UPDATE bank SET pin = ? WHERE pin = ?";
                String query2 = "UPDATE login SET pin = ? WHERE pin = ?";
                String query3 = "UPDATE SignupThree SET pin = ? WHERE pin = ?"; // Correct table name

                try (PreparedStatement pstmt1 = conn.c.prepareStatement(query1);
                     PreparedStatement pstmt2 = conn.c.prepareStatement(query2);
                     PreparedStatement pstmt3 = conn.c.prepareStatement(query3)) {

                    pstmt1.setString(1, rpin);
                    pstmt1.setString(2, pin);
                    updatedTables += pstmt1.executeUpdate() > 0 ? 1 : 0;

                    pstmt2.setString(1, rpin);
                    pstmt2.setString(2, pin);
                    updatedTables += pstmt2.executeUpdate() > 0 ? 1 : 0;

                    pstmt3.setString(1, rpin);
                    pstmt3.setString(2, pin);
                    updatedTables += pstmt3.executeUpdate() > 0 ? 1 : 0;
                }

                if (updatedTables > 0) {
                    JOptionPane.showMessageDialog(null, "PIN changed successfully");
                    setVisible(false);
                    new Transactions(rpin).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "No records updated. Please try again.");
                }
            } else if (ae.getSource() == b2) {
                new Transactions(pin).setVisible(true);
                setVisible(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PinChange("").setVisible(true);
    }
}
