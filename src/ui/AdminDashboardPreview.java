package ui;

import java.awt.Dimension;
import javax.swing.*;

public class AdminDashboardPreview {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame window =
                        new JFrame("Administrator dashboard preview");

                window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window.setContentPane(new AdminDashboardPanel());
                window.setSize(950, 600);
                window.setMinimumSize(new Dimension(780, 450));
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
}