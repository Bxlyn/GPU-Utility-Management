package ui;

import javax.swing.*;

public class DashboardPreview {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame window = new JFrame("Student dashboard preview");
                window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window.setContentPane(new StudentDashboardPanel());
                window.setSize(900, 600);
                window.setMinimumSize(new java.awt.Dimension(760, 450));
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
}