package ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Metal is included with Java: no external theme library needed.
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                } catch (Exception exception) {
                    System.err.println("Using the default Swing appearance.");
                }
                Theme.apply();
                new LoginFrame().setVisible(true);
            }
        });
    }
}
