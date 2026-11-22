package ui;

import java.awt.Dimension;
import javax.swing.*;

/** Separate integration launcher; existing login and previews stay available. */
public class SchedulerDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame window = new JFrame("GPU scheduler integration demo");
                window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window.setContentPane(new GpuMonitorPanel());
                window.setSize(1100, 700);
                window.setMinimumSize(new Dimension(1000, 580));
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            }
        });
    }
}
