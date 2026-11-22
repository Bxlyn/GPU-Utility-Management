
import java.time.LocalDateTime;

public class JobDetails {

    private int jobId;
    private String title;
    private String applicationName;
    private String status;
    private int gpuId;
    private int queuePosition;
    private LocalDateTime predictedStart;
    private LocalDateTime predictedFinish;
    private long remainingWait;

    public JobDetails(Job job, int queuePosition,
                      LocalDateTime predictedStart,
                      LocalDateTime predictedFinish,
                      long remainingWait) {

        this.jobId = job.getJobId();
        this.title = job.getTitle();
        this.applicationName = job.getApplicationName();
        this.status = job.getStatus().toString();
        this.gpuId = job.getAssignedGpuId();
        this.queuePosition = queuePosition;
        this.predictedStart = predictedStart;
        this.predictedFinish = predictedFinish;
        this.remainingWait = remainingWait;
    }

    public int getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getStatus() {
        return status;
    }

    public int getGpuId() {
        return gpuId;
    }

    public int getQueuePosition() {
        return queuePosition;
    }

    public LocalDateTime getPredictedStart() {
        return predictedStart;
    }

    public LocalDateTime getPredictedFinish() {
        return predictedFinish;
    }

    public long getRemainingWait() {
        return remainingWait;
    }

    @Override
    public String toString() {

        return "Job ID: " + jobId +
               "\nTitle: " + title +
               "\nApplication: " + applicationName +
               "\nStatus: " + status +
               "\nGPU: " + gpuId +
               "\nQueue Position: " + queuePosition +
               "\nPredicted Start: " + predictedStart +
               "\nPredicted Finish: " + predictedFinish +
               "\nRemaining Wait: " + remainingWait;
    }
}
