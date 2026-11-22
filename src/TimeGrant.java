
import java.io.Serializable;
import java.time.LocalDateTime;

public class TimeGrant implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;
    private int minutes;
    private String reason;
    private LocalDateTime grantTime;
    private String grantedBy;

    public TimeGrant(String studentId, int minutes,
                     String reason, LocalDateTime grantTime,
                     String grantedBy) {

        if (studentId == null || studentId.trim().isEmpty() ||
            minutes <= 0 ||
            reason == null || reason.trim().isEmpty() ||
            grantTime == null ||
            grantedBy == null || grantedBy.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Invalid time grant information"
            );
        }

        this.studentId = studentId;
        this.minutes = minutes;
        this.reason = reason;
        this.grantTime = grantTime;
        this.grantedBy = grantedBy;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getMinutes() {
        return minutes;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getGrantTime() {
        return grantTime;
    }

    public String getGrantedBy() {
        return grantedBy;
    }

    @Override
    public String toString() {
        return "Student ID: " + studentId +
               "\nMinutes Granted: " + minutes +
               "\nReason: " + reason +
               "\nGranted By: " + grantedBy +
               "\nGrant Time: " + grantTime;
    }
}
