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
                JTabbedPane tabs = new JTabbedPane();

                tabs.addTab("Registration Requests", new AdminDashboardPanel());
                tabs.addTab("GPU Monitor", new GpuMonitorPanel());

                window.setContentPane(tabs);
                window.setSize(1100, 700);
                window.setMinimumSize(new Dimension(1000, 580));
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
}