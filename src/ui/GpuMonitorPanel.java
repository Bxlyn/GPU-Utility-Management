package ui;

import backend.GPU;
import backend.Job;
import backend.JobStatus;
import backend.Scheduler;
import backend.TaskSize;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Locale;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** First integration: real scheduler state with explicitly seeded demo jobs. */
public class GpuMonitorPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private final Color cotton = Color.decode("#EDEBDE");
    private final Color maroon = Color.decode("#630102");
    private final DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Scheduler scheduler;
    private LocalDateTime simulationStart;
    private final JLabel utilization = new JLabel("Utilization: N/A");
    private final JButton details = new JButton("View details");
    private JTable table;
    private final JSpinner startPicker = new JSpinner(new SpinnerDateModel());
    private final JButton initialize = new JButton("Initialize clock");
    private final JButton loadDemo = new JButton("Load 5 demo jobs");
    private final JButton advance = new JButton("Advance Minute");
    private final JLabel clock = new JLabel("Simulation time: not initialized");
    private final JLabel summary = new JLabel("Initialize the clock to begin.");
    private final JLabel[] gpuStates = new JLabel[4];
    private final DefaultTableModel jobs = new DefaultTableModel(
            new String[] {"ID", "Student", "Application", "Status", "GPU",
                "Remaining", "Queue", "Predicted start", "Predicted finish", "Wait"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };

    public GpuMonitorPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(cotton);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        JLabel title = new JLabel("GPU monitor / scheduler integration");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(maroon);
        top.add(title, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        startPicker.setEditor(new JSpinner.DateEditor(startPicker, "dd/MM/yyyy HH:mm"));
        startPicker.setPreferredSize(new Dimension(180, 30));
        JLabel pickerLabel = new JLabel("Start date / time:");
        pickerLabel.setLabelFor(startPicker);
        controls.add(pickerLabel);
        controls.add(startPicker);
        for (JButton button : new JButton[] {initialize, loadDemo, advance}) {
            button.addActionListener(this);
            button.setBackground(maroon);
            button.setForeground(cotton);
            controls.add(button);
        }
        top.add(controls, BorderLayout.CENTER);
        top.add(clock, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        JPanel gpuCards = new JPanel(new GridLayout(1, 4, 12, 0));
        gpuCards.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(Color.decode("#FAF9F4"));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(3, 0, 0, 0, maroon),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)));
            JLabel label = new JLabel("GPU " + (i + 1));
            label.setForeground(maroon);
            label.setFont(new Font("SansSerif", Font.BOLD, 18));
            card.add(label, BorderLayout.NORTH);
            gpuStates[i] = new JLabel("Not initialized");
            card.add(gpuStates[i], BorderLayout.CENTER);
            gpuCards.add(card);
        }
        center.add(gpuCards, BorderLayout.NORTH);
        table = new JTable(jobs);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.decode("#FAF9F4"));
        table.setSelectionBackground(maroon);
        table.setSelectionForeground(cotton);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        int[] widths = {50, 85, 100, 100, 65, 85, 65, 150, 150, 65};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(0, 1, 0, 8));
        bottom.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        details.setBackground(maroon);
        details.setForeground(cotton);
        details.addActionListener(this);
        actions.add(details);
        bottom.add(actions);
        bottom.add(summary);
        bottom.add(utilization);
        bottom.add(new JLabel("Demo jobs: allowances, cancellation controls and file saving are not connected."));
        add(bottom, BorderLayout.SOUTH);
        loadDemo.setEnabled(false);
        advance.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == details) {
            showJobDetails();
            return;
        }
        if (event.getSource() == initialize) {
            try {
                startPicker.commitEdit();
            } catch (java.text.ParseException exception) {
                JOptionPane.showMessageDialog(this, "Enter a valid date and time.");
                return;
            }
            Date selected = (Date) startPicker.getValue();
            LocalDateTime start = LocalDateTime.ofInstant(
                    selected.toInstant(), ZoneId.systemDefault()).withSecond(0).withNano(0);
            scheduler = new Scheduler(start);
            simulationStart = start;
            startPicker.setEnabled(false);
            initialize.setEnabled(false);
            loadDemo.setEnabled(true);
            advance.setEnabled(true);
        } else if (event.getSource() == loadDemo && scheduler != null) {
            // These fixtures bypass accounts deliberately; this is not student submission.
            for (int i = 1; i <= 5; i++) {
                scheduler.submitJob(new Job(i, "1000" + i, "Demo job " + i,
                        "Python", "Five-minute scheduler demonstration",
                        TaskSize.SMALL, scheduler.getSimulationTime()));
            }
            loadDemo.setEnabled(false);
        } else if (event.getSource() == advance && scheduler != null) {
            scheduler.advanceMinute();
        }
        refreshFromScheduler();
    }

    private String time(LocalDateTime value) {
        return value == null ? "-" : format.format(value);
    }

    private void refreshFromScheduler() {
        if (scheduler == null) return;
        clock.setText("Simulation time: " + time(scheduler.getSimulationTime()));
        int busy = 0;
        for (GPU gpu : scheduler.getGpus()) {
            if (gpu.isAvailable()) {
                gpuStates[gpu.getGpuId() - 1].setText("Available");
            } else {
                busy++;
                Job job = gpu.getCurrentJob();
                gpuStates[gpu.getGpuId() - 1].setText("<html>Running job "
                        + job.getJobId() + "<br>Remaining: " + gpu.getRemainingRuntime()
                        + " min<br>Finish: " + time(scheduler.getPredictedFinish(job.getJobId()))
                        + "</html>");
            }
        }
        Integer selectedId = null;
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            selectedId = ((Number) jobs.getValueAt(
                    table.convertRowIndexToModel(selectedRow), 0)).intValue();
        }
        jobs.setRowCount(0);
        int completed = 0;
        int cancelled = 0;
        for (Job job : scheduler.getAllJobs()) {
            if (job.getStatus() == JobStatus.COMPLETED) completed++;
            if (job.getStatus() == JobStatus.CANCELLED) cancelled++;
            boolean queued = job.getStatus() == JobStatus.QUEUED;
            jobs.addRow(new Object[] {job.getJobId(), job.getStudentId(),
                job.getApplicationName(), job.getStatus(),
                job.getAssignedGpuId() < 0 ? "-" : job.getAssignedGpuId(),
                job.getRemainingRuntime(), queued ? scheduler.getQueuePosition(job.getJobId()) : "-",
                time(scheduler.getPredictedStart(job.getJobId())),
                time(scheduler.getPredictedFinish(job.getJobId())),
                queued ? scheduler.getRemainingWait(job.getJobId()) : "-"});
        }
        if (selectedId != null) {
            for (int row = 0; row < jobs.getRowCount(); row++) {
                if (((Number) jobs.getValueAt(row, 0)).intValue() == selectedId) {
                    int viewRow = table.convertRowIndexToView(row);
                    table.setRowSelectionInterval(viewRow, viewRow);
                    break;
                }
            }
        }
        utilization.setText(utilizationText());
        summary.setText("Busy GPUs: " + busy + "/4    Queued: "
                + scheduler.getWaitingJobs().size() + "    Running: " + busy
                + "    Completed: " + completed + "    Cancelled: " + cancelled);
    }

    private String utilizationText() {
        if (scheduler == null || simulationStart == null) {
            return "Utilization: N/A";
        }
        long elapsed = Duration.between(
                simulationStart, scheduler.getSimulationTime()).toMinutes();
        if (elapsed == 0) return "Utilization: N/A (no elapsed minutes)";

        // Runtime includes work already done by running and cancelled jobs.
        long busyMinutes = 0;
        for (Job job : scheduler.getAllJobs()) {
            busyMinutes += job.getElapsedRuntime();
        }
        double percent = 100.0 * busyMinutes / (4.0 * elapsed);
        return String.format(Locale.ROOT,
                "Utilization: %.2f%%    Busy GPU minutes: %d    Elapsed minutes: %d",
                percent, busyMinutes, elapsed);
    }

    private void showJobDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0 || scheduler == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a job from the table first.",
                    "No job selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        int jobId = ((Number) jobs.getValueAt(modelRow, 0)).intValue();
        Job job = scheduler.findJob(jobId);
        if (job == null) return;

        JTextArea text = new JTextArea(jobDetailsText(job), 20, 55);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(text),
                "Job " + jobId + " details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String jobDetailsText(Job job) {
        LocalDateTime waitEnd = job.getStartTime();
        if (waitEnd == null) {
            waitEnd = job.getCancellationTime() != null
                    ? job.getCancellationTime() : scheduler.getSimulationTime();
        }
        long queueWait = Duration.between(
                job.getSubmissionTime(), waitEnd).toMinutes();
        String waitLabel = job.getStatus() == JobStatus.QUEUED
                ? "Queue wait so far" : "Total queue wait";

        String result = "Job ID: " + job.getJobId()
                + "\nStudent ID: " + job.getStudentId()
                + "\nTitle: " + job.getTitle()
                + "\nApplication: " + job.getApplicationName()
                + "\nDescription: " + job.getDescription()
                + "\nTask size: " + job.getTaskSize()
                + "\nStatus: " + job.getStatus()
                + "\nGPU: " + (job.getAssignedGpuId() < 0
                        ? "Not assigned" : job.getAssignedGpuId())
                + "\n\nSubmitted: " + time(job.getSubmissionTime())
                + "\nStarted: " + time(job.getStartTime())
                + "\nFinished: " + time(job.getFinishTime())
                + "\nEstimated duration: " + job.getEstimatedDuration() + " min"
                + "\nRuntime: " + job.getElapsedRuntime() + " min"
                + "\nRemaining runtime: " + job.getRemainingRuntime() + " min"
                + "\n" + waitLabel + ": " + queueWait + " min";

        if (job.getStatus() == JobStatus.QUEUED
                || job.getStatus() == JobStatus.RUNNING) {
            result += "\nPredicted start: "
                    + time(scheduler.getPredictedStart(job.getJobId()))
                    + "\nPredicted finish: "
                    + time(scheduler.getPredictedFinish(job.getJobId()));
            if (job.getStatus() == JobStatus.QUEUED) {
                result += "\nRemaining wait: "
                        + scheduler.getRemainingWait(job.getJobId()) + " min";
            }
        }
        if (job.getStatus() == JobStatus.CANCELLED) {
            result += "\n\nCancelled: " + time(job.getCancellationTime())
                    + "\nCancelled by: " + job.getCancelledBy()
                    + "\nReason: " + job.getCancellationReason();
        }
        return result + "\n\nAllowance charged: not recorded in this demo."
                + "\nJob execution is simulated.";
    }

}
