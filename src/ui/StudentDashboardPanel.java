package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

public class StudentDashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color cotton = Color.decode("#EDEBDE");
    private final Color maroon = Color.decode("#630102");
    private final Color ink = Color.decode("#1B1716");

    public StudentDashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(cotton);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // Heading
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 8));
        heading.setOpaque(false);

        JLabel title = new JLabel("My workspace");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(maroon);

        JLabel subtitle = new JLabel(
                "Student dashboard preview — sample data");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(ink);

        heading.add(title);
        heading.add(subtitle);

        // Sample balances: 100 - 20 - 30 = 50 available minutes.
        JPanel balances = new JPanel(new GridLayout(1, 4, 16, 0));
        balances.setOpaque(false);

        balances.add(createBalance("Allocated", 100));
        balances.add(createBalance("Used", 20));
        balances.add(createBalance("Reserved", 30));
        balances.add(createBalance("Available", 50));

        JPanel top = new JPanel(new BorderLayout(0, 24));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(balances, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        add(createJobsPanel(), BorderLayout.CENTER);
    }
        

    private JPanel createBalance(String label, int minutes) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setBackground(Color.decode("#FAF9F4"));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, maroon),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel name = new JLabel(label);
        name.setFont(new Font("SansSerif", Font.PLAIN, 14));
        name.setForeground(ink);

        JLabel value = new JLabel(minutes + " min");
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(maroon);

        panel.add(name);
        panel.add(value);
        return panel;
    }
    private JPanel createJobsPanel() {
        JPanel jobsPanel = new JPanel(new BorderLayout(0, 12));
        jobsPanel.setOpaque(false);

        JLabel heading = new JLabel("My jobs");
        heading.setFont(new Font("SansSerif", Font.BOLD, 18));
        heading.setForeground(maroon);

        JButton submitButton = new JButton("Submit job");
        submitButton.setBackground(maroon);
        submitButton.setForeground(cotton);
        submitButton.setOpaque(true);

        submitButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                Window owner = SwingUtilities.getWindowAncestor(
                        StudentDashboardPanel.this);

                SubmitJobDialog dialog = new SubmitJobDialog(owner);
                dialog.setVisible(true);
            }
        });

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(heading, BorderLayout.WEST);
        toolbar.add(submitButton, BorderLayout.EAST);

        JTextField searchField = new JTextField();

        JLabel searchLabel = new JLabel("Search my jobs:");
        searchLabel.setForeground(ink);
        searchLabel.setLabelFor(searchField);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        JPanel tableHeader = new JPanel(new BorderLayout(0, 12));
        tableHeader.setOpaque(false);
        tableHeader.add(toolbar, BorderLayout.NORTH);
        tableHeader.add(searchPanel, BorderLayout.SOUTH);

        jobsPanel.add(tableHeader, BorderLayout.NORTH);

        String[] columns = {
            "Job ID", "Title", "Application", "Status",
            "GPU", "Duration (min)", "Wait (min)"
        };

        // Preview records for one student.
        // Completed durations total 20; queued reservations total 30.
        Object[][] sampleJobs = {
            {1, "Image classification", "Python", "Completed",
                "GPU 1", 15, 0},
            {2, "Matrix calculation", "MATLAB", "Completed",
                "GPU 2", 5, 0},
            {3, "Model training", "PyTorch", "Queued",
                "Not assigned", 30, 10},
            {4, "Image processing", "Python", "Cancelled",
                "Not assigned", 5, "-"}
        };

        DefaultTableModel model = new DefaultTableModel(
                sampleJobs, columns) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable jobsTable = new JTable(model);
        TableRowSorter<DefaultTableModel> sorter =
                new TableRowSorter<>(model);
        jobsTable.setRowSorter(sorter);

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

            private void filterJobs() {
                String search = searchField.getText().trim();

                if (search.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(search)));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent event) {
                filterJobs();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                filterJobs();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                filterJobs();
            }
        });
        jobsTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        jobsTable.setRowHeight(34);
        jobsTable.setForeground(ink);
        jobsTable.setBackground(Color.decode("#FAF9F4"));

        jobsTable.setSelectionBackground(maroon);
        jobsTable.setSelectionForeground(cotton);
        jobsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        jobsTable.setShowVerticalLines(false);
        jobsTable.setGridColor(Color.decode("#D5D0C4"));
        jobsTable.setFillsViewportHeight(true);

        jobsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        jobsTable.getTableHeader().setReorderingAllowed(false);

        // Preserve readable columns; scroll horizontally if needed.
        jobsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] widths = {65, 180, 110, 100, 110, 115, 90};

        for (int i = 0; i < widths.length; i++) {
            jobsTable.getColumnModel().getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(jobsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                Color.decode("#D5D0C4")));

        jobsPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel note = new JLabel(
                "Sample data only. Wait shows remaining wait for queued jobs.");
        note.setFont(new Font("SansSerif", Font.PLAIN, 12));
        note.setForeground(ink);
        JButton detailsButton = new JButton("View details");
        detailsButton.setBackground(maroon);
        detailsButton.setForeground(cotton);
        detailsButton.setOpaque(true);

        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                int selectedRow = jobsTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                            StudentDashboardPanel.this,
                            "Select a job from the table first.",
                            "No job selected",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Find the original data row after filtering or sorting.
                int modelRow = jobsTable.convertRowIndexToModel(selectedRow);

                StringBuilder details = new StringBuilder();

                for (int column = 0; column < model.getColumnCount(); column++) {
                    details.append(model.getColumnName(column))
                           .append(": ")
                           .append(model.getValueAt(modelRow, column))
                           .append("\n");
                }

                details.append("\nSample data only.");

                JTextArea detailsArea = new JTextArea(details.toString());
                detailsArea.setEditable(false);
                detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                detailsArea.setBackground(cotton);
                detailsArea.setForeground(ink);
                detailsArea.setCaretPosition(0);

                JScrollPane detailsScroll = new JScrollPane(detailsArea);
                detailsScroll.setPreferredSize(new Dimension(400, 230));

                JOptionPane.showMessageDialog(
                        StudentDashboardPanel.this,
                        detailsScroll,
                        "Job details",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(detailsButton);
        JButton cancelJobButton = new JButton("Cancel job");
        cancelJobButton.setForeground(maroon);

        cancelJobButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                int selectedRow = jobsTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                            StudentDashboardPanel.this,
                            "Select a job from the table first.",
                            "No job selected",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int modelRow = jobsTable.convertRowIndexToModel(selectedRow);
                String status = model.getValueAt(modelRow, 3).toString();
                Object jobId = model.getValueAt(modelRow, 0);

                if (!status.equals("Queued") && !status.equals("Running")) {
                    JOptionPane.showMessageDialog(
                            StudentDashboardPanel.this,
                            "Only queued or running jobs can be cancelled.",
                            "Cannot cancel job",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String chargeRule;

                if (status.equals("Queued")) {
                    chargeRule = "Queued jobs have no cancellation charge.";
                } else {
                    chargeRule = "Running jobs are charged for elapsed runtime only.";
                }

                JTextArea reasonField = new JTextArea(4, 25);
                reasonField.setLineWrap(true);
                reasonField.setWrapStyleWord(true);

                Object[] message = {
                    "Job ID: " + jobId,
                    chargeRule,
                    "The full reservation is released on cancellation.",
                    "Enter a cancellation reason:",
                    new JScrollPane(reasonField),
                    "Preview only: this will not cancel the job."
                };

                while (true) {
                    int choice = JOptionPane.showConfirmDialog(
                            StudentDashboardPanel.this,
                            message,
                            "Review cancellation",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (choice != JOptionPane.OK_OPTION) {
                        return;
                    }

                    if (reasonField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(
                                StudentDashboardPanel.this,
                                "Please enter a cancellation reason.",
                                "Missing reason",
                                JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    // Later, pass the job ID and reason to the backend.
                    JOptionPane.showMessageDialog(
                            StudentDashboardPanel.this,
                            "Cancellation preview checked for job " + jobId
                            + ".\nNo job or balance has been changed.",
                            "Preview only",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        });

        actions.add(Box.createHorizontalStrut(10));
        actions.add(cancelJobButton);

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(note, BorderLayout.SOUTH);

        jobsPanel.add(bottom, BorderLayout.SOUTH);
        

        return jobsPanel;
    }
}