package backend;

public class JobStatus {

    private String status;

    public static final JobStatus QUEUED = new JobStatus("Queued");
    public static final JobStatus RUNNING = new JobStatus("Running");
    public static final JobStatus COMPLETED = new JobStatus("Completed");
    public static final JobStatus CANCELLED = new JobStatus("Cancelled");

    private JobStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
