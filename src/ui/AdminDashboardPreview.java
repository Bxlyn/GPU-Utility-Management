package ui;

import java.time.LocalDateTime;
import javax.swing.*;

// Standalone launcher for the admin panels.
public class AdminDashboardPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext.startNew(LocalDateTime.now().withSecond(0).withNano(0));
            AppContext.get().loginAdmin("admin", "gpu2026");
            JFrame window = new JFrame("Administrator dashboard preview");
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Registration Requests", new AdminDashboardPanel());
            tabs.addTab("GPU Monitor", new GpuMonitorPanel());
            window.setContentPane(tabs);
            window.setSize(1100, 700);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
