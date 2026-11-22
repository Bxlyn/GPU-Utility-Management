package ui;

import java.awt.*;
import model.Student;
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
                "Registration requests and approved students — live data");
        subtitle.setForeground(ink);

        heading.add(title);
        heading.add(subtitle);
        add(heading, BorderLayout.NORTH);

        String[] columns = {
            "University ID", "Name", "Email", "Phone", "Status"
        };

        requestsModel = new DefaultTableModel(columns, 0) {
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
                "Changes are saved to the shared application state.");
        note.setForeground(ink);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(note, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
        refreshRequests();
    }

    public void refreshRequests() {
        requestsModel.setRowCount(0);
        for (Student student : AppContext.get().getPendingRequests()) {
            addRequestRow(student, "Pending");
        }
        for (Student student : AppContext.get().getStudents()) {
            addRequestRow(student, "Approved");
        }
        for (Student student : AppContext.get().getRejectedRequests()) {
            addRequestRow(student, "Rejected");
        }
    }

    private void addRequestRow(Student s, String status) {
        requestsModel.addRow(new Object[] {
            s.getUsername(), s.getName(), s.getEmail(), s.getPhone(), status
        });
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
        Object[] message = { "Student: " + studentId,
            "Initial allowance (minutes):", minutesField };
        int choice = JOptionPane.showConfirmDialog(this, message,
            "Approve student", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;
        try {
            int minutes = Integer.parseInt(minutesField.getText().trim());
            if (minutes < 0) throw new NumberFormatException();
            AppContext.get().approveStudent(studentId);
            AppContext.get().adjustAllowance(studentId, minutes);
            AppContext.save();
            refreshRequests();
            JOptionPane.showMessageDialog(this, "Student approved: " + studentId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a non-negative whole number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Approval failed: " + ex.getMessage());
        }
    }

    private void previewRejection(String studentId) {
        int choice = JOptionPane.showConfirmDialog(this,
            "Reject request for " + studentId + "?",
            "Reject request", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            AppContext.get().rejectStudent(studentId);
            AppContext.save();
            refreshRequests();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Rejection failed: " + ex.getMessage());
        }
    }

    private void previewGrant(String studentId) {
        JTextField minutesField = new JTextField(10);
        JTextField reasonField = new JTextField(25);
        Object[] message = {"Student: " + studentId,
            "Additional GPU minutes:", minutesField,
            "Reason:", reasonField};
        int choice = JOptionPane.showConfirmDialog(this, message,
            "Grant GPU minutes", JOptionPane.OK_CANCEL_OPTION);
        if (choice != JOptionPane.OK_OPTION) return;
        try {
            int minutes = Integer.parseInt(minutesField.getText().trim());
            String reason = reasonField.getText().trim();
            AppContext.get().grantExtraTime(studentId, minutes, reason);
            AppContext.save();
            refreshRequests();
            JOptionPane.showMessageDialog(this, "Granted " + minutes + " minutes to " + studentId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a positive whole number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Grant failed: " + ex.getMessage());
        }
    }
}
