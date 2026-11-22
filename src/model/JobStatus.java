package model;
import java.io.Serializable;
import java.io.ObjectStreamException;

public class JobStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;

    public static final JobStatus QUEUED = new JobStatus("Queued");
    public static final JobStatus RUNNING = new JobStatus("Running");
    public static final JobStatus COMPLETED = new JobStatus("Completed");
    public static final JobStatus CANCELLED = new JobStatus("Cancelled");

    private JobStatus(String status) {
        this.status = status;
    }


    private Object readResolve() throws ObjectStreamException {
        if ("Queued".equals(status)) {
            return QUEUED;
        }
        if ("Running".equals(status)) {
            return RUNNING;
        }
        if ("Completed".equals(status)) {
            return COMPLETED;
        }
        if ("Cancelled".equals(status)) {
            return CANCELLED;
        }

        throw new java.io.InvalidObjectException(
            "Unknown job status"
        );
    }


    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
