package ui;

import dto.DashboardSummary;
import dto.JobDetails;
import model.GPU;
import model.Job;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Live administrator GPU monitor backed by the shared application state. */
public class GpuMonitorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Color cotton = Color.decode("#EDEBDE");
    private final Color maroon = Color.decode("#630102");
    private final JLabel[] gpuStates = new JLabel[4];
    private final JLabel clock = new JLabel();
    private final JLabel summary = new JLabel();
    private final JLabel utilization = new JLabel();
    private final DefaultTableModel jobs = new DefaultTableModel(
        new String[] {"ID", "Student", "Application", "Status", "GPU",
            "Remaining", "Queue", "Predicted start", "Predicted finish", "Wait"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(jobs);

    public GpuMonitorPanel() {
        setLayout(new BorderLayout(0, 14));
        setBackground(cotton);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        JLabel title = new JLabel("GPU monitor / live scheduler");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(maroon);
        top.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setOpaque(false);
        JButton advance = new JButton("Advance Minute");
        JButton save = new JButton("Save");
        JButton refresh = new JButton("Refresh");
        JButton cancel = new JButton("Cancel selected job");
        for (JButton button : new JButton[] {advance, save, refresh, cancel}) {
            buttons.add(button);
        }
        advance.addActionListener(e -> {
            try {
                AppContext.get().advanceMinute();
                AppContext.save();
                refreshData();
            } catch (Exception ex) { showError(ex); }
        });
        save.addActionListener(e -> {
            try { AppContext.save(); JOptionPane.showMessageDialog(this, "Saved."); }
            catch (Exception ex) { showError(ex); }
        });
        refresh.addActionListener(e -> refreshData());
        cancel.addActionListener(e -> cancelSelectedJob());
        top.add(buttons, BorderLayout.CENTER);
        top.add(clock, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 4, 10, 0));
        cards.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            JPanel card = new JPanel(new BorderLayout(0, 6));
            card.setBackground(Color.decode("#FAF9F4"));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, maroon),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
            JLabel name = new JLabel("GPU " + (i + 1));
            name.setForeground(maroon);
            name.setFont(new Font("SansSerif", Font.BOLD, 18));
            gpuStates[i] = new JLabel("Available");
            card.add(name, BorderLayout.NORTH);
            card.add(gpuStates[i], BorderLayout.CENTER);
            cards.add(card);
        }
        center.add(cards, BorderLayout.NORTH);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 90, 110, 95, 60, 85, 70, 150, 150, 65};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setOpaque(false);
        JButton details = new JButton("View selected job details");
        details.addActionListener(e -> showSelectedJob());
        bottom.add(details, BorderLayout.NORTH);
        JPanel statistics = new JPanel(new GridLayout(2, 1));
        statistics.setOpaque(false);
        statistics.add(summary);
        statistics.add(utilization);
        bottom.add(statistics, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
        refreshData();
    }

    private JobDetails selectedJob() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a job first.");
            return null;
        }
        int jobId = ((Number) jobs.getValueAt(table.convertRowIndexToModel(row), 0)).intValue();
        for (JobDetails detail : AppContext.get().getAllJobDetails()) {
            if (detail.getJobId() == jobId) return detail;
        }
        return null;
    }

    private void showSelectedJob() {
        JobDetails selected = selectedJob();
        if (selected == null) return;
        JTextArea text = new JTextArea(selected.toString(), 21, 55);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(text),
            "Job " + selected.getJobId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSelectedJob() {
        JobDetails selected = selectedJob();
        if (selected == null) return;
        String reason = JOptionPane.showInputDialog(this, "Cancellation reason:");
        if (reason == null) return;
        try {
            AppContext.get().cancelAdminJob(selected.getJobId(), reason.trim());
            AppContext.save();
            refreshData();
        } catch (Exception ex) { showError(ex); }
    }

    public void refreshData() {
        try {
            clock.setText("Simulation time: " + AppContext.get().getSimulationTime()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            for (GPU gpu : AppContext.get().getGpus()) {
                int index = gpu.getGpuId() - 1;
                if (gpu.isAvailable()) {
                    gpuStates[index].setText("Available");
                } else {
                    Job job = gpu.getCurrentJob();
                    gpuStates[index].setText("<html>Job " + job.getJobId()
                        + "<br>Remaining: " + gpu.getRemainingRuntime()
                        + " min<br>Finish: " + AppContext.get()
                        .getGpuExpectedFinishText(gpu.getGpuId()) + "</html>");
                }
            }
            jobs.setRowCount(0);
            for (JobDetails detail : AppContext.get().getAllJobDetails()) {
                boolean queued = "Queued".equals(detail.getStatus());
                jobs.addRow(new Object[] {
                    detail.getJobId(), detail.getStudentId(), detail.getApplicationName(),
                    detail.getStatus(), detail.getGpuId(), detail.getRemainingRuntime(),
                    detail.getQueuePosition(), detail.getPredictedStart(),
                    detail.getPredictedFinish(),
                    queued ? detail.getRemainingWait() : "N/A"
                });
            }
            DashboardSummary data = AppContext.get().getDashboardSummary();
            summary.setText("Busy GPUs: " + data.getBusyGpus() + "/4    Queued: "
                + data.getQueuedJobs() + "    Running: " + data.getRunningJobs()
                + "    Completed: " + data.getCompletedJobs()
                + "    Cancelled: " + data.getCancelledJobs());
            double pct = data.getUtilization();
            utilization.setText("Utilization: " + (pct < 0 ? "N/A"
                : String.format("%.2f%%", pct)));
        } catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
