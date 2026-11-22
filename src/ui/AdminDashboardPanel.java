package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class AdminDashboardPanel extends JPanel
        implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Color cotton = Color.decode("#EDEBDE");
    private final Color maroon = Color.decode("#630102");
    private final Color ink = Color.decode("#1B1716");

    private final JTable requestsTable;
    private final DefaultTableModel requestsModel;

    private final JButton approveButton = new JButton("Approve");
    private final JButton rejectButton = new JButton("Reject");
    private final JButton grantButton = new JButton("Grant minutes");

    public AdminDashboardPanel() {
        setLayout(new BorderLayout(0, 24));
        setBackground(cotton);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 8));
        heading.setOpaque(false);

        JLabel title = new JLabel("Administrator workspace");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(maroon);

        JLabel subtitle = new JLabel(
                "Registration requests and student search — sample data");
        subtitle.setForeground(ink);

        heading.add(title);
        heading.add(subtitle);
        add(heading, BorderLayout.NORTH);

        String[] columns = {
            "University ID", "Name", "Email", "Phone", "Status"
        };

        Object[][] sampleRequests = {
            {"10001", "Ali Hassan", "ali@example.com",
                "0501234567", "Pending"},
            {"10002", "Sara Ahmed", "sara@example.com",
                "0502345678", "Pending"},
            {"10003", "Omar Khalid", "omar@example.com",
                "0503456789", "Approved"},
            {"10004", "Noor Salem", "noor@example.com",
                "0504567890", "Rejected"}
        };

        requestsModel = new DefaultTableModel(
                sampleRequests, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestsTable = new JTable(requestsModel);
        requestsTable.setRowHeight(34);
        requestsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        requestsTable.setBackground(Color.decode("#FAF9F4"));
        requestsTable.setForeground(ink);
        requestsTable.setSelectionBackground(maroon);
        requestsTable.setSelectionForeground(cotton);
        requestsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        requestsTable.setFillsViewportHeight(true);
        requestsTable.setShowVerticalLines(false);
        requestsTable.getTableHeader().setReorderingAllowed(false);

        requestsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {120, 160, 220, 130, 100};

        for (int i = 0; i < widths.length; i++) {
            requestsTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(requestsModel);
        requestsTable.setRowSorter(sorter);

        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        // Search only the university ID column, using an exact match.
        ActionListener searchAction = event -> {
            String studentId = searchField.getText().trim();

            if (studentId.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(
                        "^" + Pattern.quote(studentId) + "$", 0));
            }
        };

        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);

        clearButton.addActionListener(event -> {
            searchField.setText("");
            sorter.setRowFilter(null);
        });

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("University ID:");
        searchLabel.setForeground(ink);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(searchPanel, BorderLayout.NORTH);
        content.add(new JScrollPane(requestsTable), BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        approveButton.setBackground(maroon);
        approveButton.setForeground(cotton);
        approveButton.setOpaque(true);

        rejectButton.setForeground(maroon);

        grantButton.setBackground(maroon);
        grantButton.setForeground(cotton);
        grantButton.setOpaque(true);

        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        grantButton.addActionListener(this);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(grantButton);
        buttons.add(rejectButton);
        buttons.add(approveButton);

        JLabel note = new JLabel(
                "Preview only. Account statuses and allowances are unchanged.");
        note.setForeground(ink);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(note, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int selectedRow = requestsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a student from the table first.",
                    "No student selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Sorting and filtering can change the visible row order.
        int modelRow =
                requestsTable.convertRowIndexToModel(selectedRow);

        String status =
                requestsModel.getValueAt(modelRow, 4).toString();

        String studentId =
                requestsModel.getValueAt(modelRow, 0).toString();

        if (event.getSource() == grantButton) {
            if (!status.equals("Approved")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Extra minutes can only be granted to approved students.",
                        "Student not approved",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            previewGrant(studentId);
            return;
        }

        if (!status.equals("Pending")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only pending requests can be approved or rejected.",
                    "Request already reviewed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (event.getSource() == approveButton) {
            previewApproval(studentId);
        } else if (event.getSource() == rejectButton) {
            previewRejection(studentId);
        }
    }

    private void previewApproval(String studentId) {
        JTextField minutesField = new JTextField("100", 10);

        Object[] message = {
            "University ID: " + studentId,
            "Initial allowance in GPU minutes:",
            minutesField,
            "Preview only. This will not activate the account."
        };

        while (true) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Review approval",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            int minutes;

            try {
                minutes = Integer.parseInt(
                        minutesField.getText().trim());

                if (minutes < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a whole number of zero or more.",
                        "Invalid allowance",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            // Later, call the backend approval method here.
            JOptionPane.showMessageDialog(
                    this,
                    "Approval preview for student " + studentId
                    + "\nInitial allowance: " + minutes + " minutes"
                    + "\n\nNo account or allowance has been changed.",
                    "Preview only",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void previewRejection(String studentId) {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Review rejection of student " + studentId + "?"
                + "\nPreview only. The request will remain unchanged.",
                "Review rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Later, call the backend rejection method here.
            JOptionPane.showMessageDialog(
                    this,
                    "Rejection preview checked."
                    + "\nNo request has been changed.",
                    "Preview only",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void previewGrant(String studentId) {
        JTextField minutesField = new JTextField(10);
        JTextField reasonField = new JTextField(25);

        Object[] message = {
            "University ID: " + studentId,
            "Extra GPU minutes:",
            minutesField,
            "Reason:",
            reasonField,
            "Preview only. The allowance will remain unchanged."
        };

        while (true) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Grant minutes preview",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                return;
            }

            int minutes;

            try {
                minutes = Integer.parseInt(
                        minutesField.getText().trim());

                if (minutes <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a positive whole number of minutes.",
                        "Invalid minutes",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            String reason = reasonField.getText().trim();

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Enter a reason for granting extra minutes.",
                        "Missing reason",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }

            // Later, call the backend grant method here.
            JOptionPane.showMessageDialog(
                    this,
                    "University ID: " + studentId
                    + "\nExtra minutes: " + minutes
                    + "\nReason: " + reason
                    + "\n\nPreview only. No allowance has been changed.",
                    "Grant preview",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }
}