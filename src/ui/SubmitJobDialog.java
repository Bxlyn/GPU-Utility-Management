package ui;

import java.awt.*;
import model.Job;
import model.TaskSize;
import java.awt.event.*;
import javax.swing.*;

// Collects job details and sends the request to the backend.
public class SubmitJobDialog extends JDialog
        implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final Color cotton = Color.decode("#EDEBDE");
    private final Color maroon = Color.decode("#630102");
    private final Color ink = Color.decode("#1B1716");

    private final JTextField titleField = new JTextField();
    private final JTextField applicationField = new JTextField();
    private final JTextArea descriptionField = new JTextArea(4, 25);

    private final JComboBox<String> sizeBox = new JComboBox<>(
            new String[] {"Small", "Medium", "Large", "Very large"});

    private final int[] durations = {5, 15, 30, 60};
    private final String studentId;
    private final Runnable onSubmitted;

    private final JLabel durationLabel = new JLabel("Duration: 5 minutes");
    private final JButton reviewButton = new JButton("Review job");
    private final JButton cancelButton = new JButton("Cancel");

    public SubmitJobDialog(Window owner, String studentId, Runnable onSubmitted) {
        super(owner, "Submit a job", ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.onSubmitted = onSubmitted;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(cotton);
        content.setBorder(
                BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setContentPane(content);

        JLabel heading = new JLabel("New GPU job");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setForeground(maroon);
        content.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        form.add(createField("Job title", titleField));
        form.add(Box.createVerticalStrut(12));

        form.add(createField("Application name", applicationField));
        form.add(Box.createVerticalStrut(12));

        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        form.add(createField(
                "Description", new JScrollPane(descriptionField)));
        form.add(Box.createVerticalStrut(12));

        form.add(createField("Task size", sizeBox));
        form.add(Box.createVerticalStrut(10));

        durationLabel.setForeground(maroon);
        durationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(durationLabel);

        content.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);

        JLabel note = new JLabel(
                "Submitting reserves the full duration immediately.");
        note.setForeground(ink);
        bottom.add(note, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);

        reviewButton.setBackground(maroon);
        reviewButton.setForeground(cotton);
        reviewButton.setOpaque(true);

        buttons.add(cancelButton);
        buttons.add(reviewButton);
        bottom.add(buttons, BorderLayout.SOUTH);

        content.add(bottom, BorderLayout.SOUTH);

        sizeBox.addActionListener(this);
        reviewButton.addActionListener(this);
        cancelButton.addActionListener(this);

        getRootPane().setDefaultButton(reviewButton);

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
    }

    private JPanel createField(String text, JComponent input) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(text);
        label.setForeground(ink);

        if (input instanceof JScrollPane) {
            label.setLabelFor(descriptionField);
        } else {
            label.setLabelFor(input);
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sizeBox) {
            int minutes = durations[sizeBox.getSelectedIndex()];
            durationLabel.setText("Duration: " + minutes + " minutes");

        } else if (event.getSource() == cancelButton) {
            dispose();

        } else if (event.getSource() == reviewButton) {
            reviewJob();
        }
    }

    // Check the form before asking the backend to submit the job.
    private void reviewJob() {
        String title = titleField.getText().trim();
        String appName = applicationField.getText().trim();
        String description = descriptionField.getText().trim();
        if (title.isEmpty() || appName.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Enter a title, application name, and description.");
            return;
        }

        int i = sizeBox.getSelectedIndex();
        TaskSize size = i == 0 ? TaskSize.SMALL
            : i == 1 ? TaskSize.MEDIUM
            : i == 2 ? TaskSize.LARGE : TaskSize.VERY_LARGE;
        int answer = JOptionPane.showConfirmDialog(this,
            "Submit this job?\nTitle: " + title + "\nApplication: " + appName
            + "\nTask size: " + size + "\nDuration: " + size.getDuration()
            + " GPU minutes", "Confirm job", JOptionPane.OK_CANCEL_OPTION);
        if (answer != JOptionPane.OK_OPTION) return;
        try {
            Job job = AppContext.get().submitJob(
                studentId, title, appName, description, size);
            AppContext.save();
            JOptionPane.showMessageDialog(this,
                "Job " + job.getJobId() + " submitted successfully.");
            if (onSubmitted != null) onSubmitted.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Could not submit job: " + ex.getMessage(),
                "Submission failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
