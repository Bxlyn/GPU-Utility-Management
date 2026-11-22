import ui.AppContext;
import ui.LoginFrame;
import ui.Theme;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.apply();
            try {
                boolean loadExisting = false;
                if (AppContext.savedDataExists()) {
                    int answer = JOptionPane.showConfirmDialog(null,
                        "Saved GPU simulation found. Restore it?\n" +
                        "Choose No to create a new simulation.",
                        "GPU Lab Manager", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (answer == JOptionPane.CANCEL_OPTION ||
                        answer == JOptionPane.CLOSED_OPTION) return;
                    loadExisting = (answer == JOptionPane.YES_OPTION);
                }
                if (loadExisting) {
                    AppContext.load();
                } else {
                    JSpinner datePicker = new JSpinner(new SpinnerDateModel());
                    JSpinner timePicker = new JSpinner(new SpinnerDateModel());
                    datePicker.setEditor(new JSpinner.DateEditor(datePicker, "dd/MM/yyyy"));
                    timePicker.setEditor(new JSpinner.DateEditor(timePicker, "HH:mm"));
                    int answer = JOptionPane.showConfirmDialog(null,
                        new Object[] {"Simulation start date:", datePicker,
                            "Simulation start time (24-hour):", timePicker},
                        "Initialize simulation clock", JOptionPane.OK_CANCEL_OPTION);
                    if (answer != JOptionPane.OK_OPTION) return;
                    try {
                        datePicker.commitEdit();
                        timePicker.commitEdit();
                    } catch (java.text.ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid date or time.");
                        return;
                    }
                    LocalDateTime chosenDate = LocalDateTime.ofInstant(
                        ((Date) datePicker.getValue()).toInstant(), ZoneId.systemDefault());
                    LocalDateTime chosenTime = LocalDateTime.ofInstant(
                        ((Date) timePicker.getValue()).toInstant(), ZoneId.systemDefault());
                    LocalDateTime start = LocalDateTime.of(
                        chosenDate.toLocalDate(), chosenTime.toLocalTime())
                        .withSecond(0).withNano(0);
                    AppContext.startNew(start);
                }
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Startup failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
