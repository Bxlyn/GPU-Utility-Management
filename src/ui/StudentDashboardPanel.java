package ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import dto.JobDetails;
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
    private final String studentId;
    private final Map<String, JLabel> balanceLabels = new HashMap<>();
    private DefaultTableModel jobsModel;

    public StudentDashboardPanel(String studentId) {
        this.studentId = studentId;
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
                "My GPU jobs and allowance — live data");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(ink);

        heading.add(title);
        heading.add(subtitle);

        // Initial placeholders are replaced by refreshData().
        JPanel balances = new JPanel(new GridLayout(1, 4, 16, 0));
        balances.setOpaque(false);

        balances.add(createBalance("Allocated", 0));
        balances.add(createBalance("Used", 0));
        balances.add(createBalance("Reserved", 0));
        balances.add(createBalance("Available", 0));

        JPanel top = new JPanel(new BorderLayout(0, 24));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(balances, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        add(createJobsPanel(), BorderLayout.CENTER);
        refreshData();
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
        balanceLabels.put(label, value);
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

                SubmitJobDialog dialog = new SubmitJobDialog(
                    owner, studentId, StudentDashboardPanel.this::refreshData);
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

        Object[][] sampleJobs = new Object[0][columns.length];

        DefaultTableModel model = new DefaultTableModel(
                sampleJobs, columns) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jobsModel = model;
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
                "Wait shows predicted remaining waiting time for queued jobs.");
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

                 int jobId = ((Number) model.getValueAt(modelRow, 0)).intValue();
                 StringBuilder details = new StringBuilder();
                 for (JobDetails item : AppContext.get().getStudentJobDetails(studentId)) {
                     if (item.getJobId() == jobId) {
                         details.append(item.toString());
                         break;
                     }
                 }

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
                    "Cancellation will release the job reservation."
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

                    try {
                        AppContext.get().cancelStudentJob(studentId,
                            ((Number) jobId).intValue(), reasonField.getText().trim());
                        AppContext.save();
                        refreshData();
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Job " + jobId + " cancelled.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                            "Cancellation failed: " + ex.getMessage());
                    }
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
    // Re-read live balances and job records after every action.
    public void refreshData() {
        try {
            balanceLabels.get("Allocated").setText(
                AppContext.get().getStudentAllocatedMinutes(studentId) + " min");
            balanceLabels.get("Used").setText(
                AppContext.get().getStudentUsedMinutes(studentId) + " min");
            balanceLabels.get("Reserved").setText(
                AppContext.get().getStudentReservedMinutes(studentId) + " min");
            balanceLabels.get("Available").setText(
                AppContext.get().getAvailableMinutes(studentId) + " min");

            jobsModel.setRowCount(0);
            for (JobDetails job : AppContext.get().getStudentJobDetails(studentId)) {
                jobsModel.addRow(new Object[] {
                    job.getJobId(), job.getTitle(), job.getApplicationName(),
                    job.getStatus(), job.getGpuId(), job.getDuration(),
                    "Queued".equals(job.getStatus()) ? job.getRemainingWait() : "N/A"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Could not refresh student records: " + ex.getMessage());
        }
    }
}