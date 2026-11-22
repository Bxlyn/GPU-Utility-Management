package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/** Shared styling keeps the two forms consistent. */
public final class Theme {
    public static final Color BACKGROUND = new Color(243, 245, 247);
    public static final Color INK = new Color(36, 49, 62);
    public static final Color MUTED = new Color(94, 108, 121);
    public static final Color BLUE = new Color(43, 88, 125);
    public static final Color LINE = new Color(211, 218, 224);

    private Theme() { }

    // Apply the same fonts and colors across Swing screens.
    public static void apply() {
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        String[] keys = {"Label.font", "Button.font", "TextField.font",
                "PasswordField.font", "CheckBox.font", "OptionPane.messageFont"};
        for (String key : keys) UIManager.put(key, font);
        UIManager.put("Label.foreground", INK);
        UIManager.put("Button.gradient", null);
    }

    public static JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    public static JButton button(String text, boolean primary) {
        JButton button = new JButton(text);
        button.setBackground(primary ? BLUE : Color.WHITE);
        button.setForeground(primary ? Color.WHITE : BLUE);
        button.setOpaque(true);
        button.setBorder(new CompoundBorder(new LineBorder(primary ? BLUE : LINE),
                new EmptyBorder(10, 18, 10, 18)));
        // Keep keyboard focus painting enabled.
        return button;
    }

    public static JPanel field(String title, JTextField input) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel label = label(title, 13, INK);
        label.setLabelFor(input);
        input.setPreferredSize(new Dimension(320, 36));
        input.setBorder(new CompoundBorder(new LineBorder(LINE),
                new EmptyBorder(7, 10, 7, 10)));
        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }
}
