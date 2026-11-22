package ui;

import java.time.LocalDateTime;
import javax.swing.*;

// Standalone launcher for the student dashboard.
public class DashboardPreview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext.startNew(LocalDateTime.now().withSecond(0).withNano(0));
            AppContext.get().registerStudent("10001", "test123", "Demo Student",
                "demo@example.com", "0501234567");
            AppContext.get().loginAdmin("admin", "gpu2026");
            AppContext.get().approveStudent("10001");
            AppContext.get().logoutAdmin();
            AppContext.get().loginStudent("10001", "test123");
            JFrame window = new JFrame("Student dashboard preview");
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setContentPane(new StudentDashboardPanel("10001"));
            window.setSize(1000, 700);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
