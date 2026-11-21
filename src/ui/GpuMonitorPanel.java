package ui;

import backend.GPU;
import backend.Job;
import backend.JobStatus;
import backend.Scheduler;
import backend.TaskSize;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
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
        JTable table = new JTable(jobs);
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
        bottom.add(summary);
        bottom.add(new JLabel("Demo session: real scheduling; no login, allowances, cancellation or file saving connected."));
        add(bottom, BorderLayout.SOUTH);
        loadDemo.setEnabled(false);
        advance.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
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
        summary.setText("Busy GPUs: " + busy + "/4    Queued: "
                + scheduler.getWaitingJobs().size() + "    Running: " + busy
                + "    Completed: " + completed + "    Cancelled: " + cancelled);
    }
}
