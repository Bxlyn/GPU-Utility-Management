package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;

/** Front-end starter. Authentication and account storage are not implemented here. */
public class LoginFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final CardLayout screens = new CardLayout();
    private final JPanel content = new JPanel(screens);
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JTextField universityId = new JTextField();
    private final JTextField studentName = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField phone = new JTextField();
    private final JPasswordField newPassword = new JPasswordField();
    private final JButton signIn = Theme.button("Sign in", true);
    private final JButton register = Theme.button("Create an account", false);
    private final JButton submit = Theme.button("Submit request", true);
    private final JButton back = Theme.button("Back to sign in", false);
    private final JCheckBox showPassword = new JCheckBox("Show password");
    private final char passwordMask = password.getEchoChar();
    private final JLabel loginMessage = Theme.label(" ", 12, Theme.MUTED);
    private final JLabel registrationMessage = Theme.label(" ", 12, Theme.MUTED);

    public LoginFrame() {
        super("GPU Lab Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 720));
        setSize(920, 790);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Theme.LINE),
                new EmptyBorder(18, 28, 18, 28)));
        header.add(Theme.label("GPU LAB / RESOURCE MANAGEMENT", 14, Theme.INK), BorderLayout.WEST);
        header.add(Theme.label("COSC 230", 12, Theme.MUTED), BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        content.add(loginScreen(), "login");
        content.add(registrationScreen(), "register");
        add(content, BorderLayout.CENTER);
        JLabel footer = Theme.label("Four GPUs. One shared queue.", 12, Theme.MUTED);
        footer.setBorder(new EmptyBorder(12, 28, 18, 28));
        getContentPane().setBackground(Theme.BACKGROUND);
        add(footer, BorderLayout.SOUTH);

        signIn.addActionListener(this);
        register.addActionListener(this);
        submit.addActionListener(this);
        back.addActionListener(this);
        showPassword.addActionListener(this);
        getRootPane().setDefaultButton(signIn);
    }

    // Each card groups its heading, form and actions with layout managers.
    private JPanel card(String heading, String subtitle, JPanel form, JPanel actions,
                        JLabel message) {
        JPanel card = new JPanel(new BorderLayout(0, 22));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(Theme.LINE),
                new EmptyBorder(28, 32, 24, 32)));
        JPanel headingPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        headingPanel.setOpaque(false);
        headingPanel.add(Theme.label(heading, 27, Theme.INK));
        headingPanel.add(Theme.label(subtitle, 13, Theme.MUTED));
        card.add(headingPanel, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(0, 14));
        bottom.setOpaque(false);
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(message, BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);
        JPanel surrounding = new JPanel(new GridBagLayout());
        surrounding.setBackground(Theme.BACKGROUND);
        surrounding.setBorder(new EmptyBorder(20, 20, 20, 20));
        surrounding.add(card);
        return surrounding;
    }

    private JPanel loginScreen() {
        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.setOpaque(false);
        JPanel fields = new JPanel(new GridLayout(2, 1, 0, 16));
        fields.setOpaque(false);
        fields.add(Theme.field("University ID / admin username", username));
        fields.add(Theme.field("Password", password));
        form.add(fields, BorderLayout.CENTER);
        showPassword.setOpaque(false);
        form.add(showPassword, BorderLayout.SOUTH);
        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 10));
        actions.setOpaque(false);
        actions.add(signIn);
        actions.add(register);
        return card("Welcome back", "Sign in to manage your GPU jobs.", form, actions, loginMessage);
    }

    private JPanel registrationScreen() {
        JPanel form = new JPanel(new GridLayout(5, 1, 0, 12));
        form.setOpaque(false);
        form.add(Theme.field("University ID", universityId));
        form.add(Theme.field("Full name", studentName));
        form.add(Theme.field("Email address", email));
        form.add(Theme.field("Phone number", phone));
        form.add(Theme.field("Choose a password", newPassword));
        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.add(back);
        actions.add(submit);
        return card("Request an account", "An administrator must approve your registration.",
                form, actions, registrationMessage);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == register || source == back) {
            boolean registering = source == register;
            password.setText("");
            newPassword.setText("");
            showPassword.setSelected(false);
            password.setEchoChar(passwordMask);
            loginMessage.setText(" ");
            registrationMessage.setText(" ");
            screens.show(content, registering ? "register" : "login");
            getRootPane().setDefaultButton(registering ? submit : signIn);
            (registering ? universityId : username).requestFocusInWindow();
        } else if (source == showPassword) {
            password.setEchoChar(showPassword.isSelected() ? (char) 0 : passwordMask);
        } else if (source == signIn) {
            attemptLogin();
        } else if (source == submit) {
            attemptRegistration();
        }
    }

    private void attemptLogin() {
        char[] enteredPassword = password.getPassword();
        try {
            if (username.getText().trim().isEmpty() || enteredPassword.length == 0) {
                error(loginMessage, "Enter your username and password.");
                return;
            }
            // BACK-END CONNECTION: call your friend's login method here.
            // On success, open the dashboard for the returned user's role.
            // Do not treat non-empty fields as successful authentication.
            loginMessage.setForeground(Theme.MUTED);
            loginMessage.setText("Form ready. Authentication is not connected yet.");
        } finally {
            Arrays.fill(enteredPassword, '\0');
        }
    }

    private void attemptRegistration() {
        char[] enteredPassword = newPassword.getPassword();
        try {
            if (universityId.getText().trim().isEmpty() || studentName.getText().trim().isEmpty()
                    || email.getText().trim().isEmpty() || phone.getText().trim().isEmpty()
                    || enteredPassword.length == 0) {
                error(registrationMessage, "Complete all five fields before submitting.");
                return;
            }
            if (!email.getText().trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
                error(registrationMessage, "Enter an email such as name@example.com.");
                return;
            }
            // BACK-END CONNECTION: pass the five values to registerStudent(...).
            // The back end must check duplicates and save a pending request.
            // Only show a success message after that method succeeds.
            registrationMessage.setForeground(Theme.MUTED);
            registrationMessage.setText("Form ready. No request saved; back end not connected.");
        } finally {
            Arrays.fill(enteredPassword, '\0');
        }
    }

    private void error(JLabel label, String message) {
        label.setForeground(new Color(161, 49, 49));
        label.setText(message);
    }
}
