
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class JobDetails {

    private int jobId;
    private String studentId;
    private String title;
    private String applicationName;
    private String description;
    private String taskSize;
    private String status;

    private int gpuId;
    private int duration;
    private int remainingRuntime;
    private int elapsedRuntime;
    private int allowanceCharged;
    private int queuePosition;

    private LocalDateTime submissionTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime predictedStart;
    private LocalDateTime predictedFinish;
    private LocalDateTime cancellationTime;

    private long remainingWait;
    private String cancelledBy;
    private String cancellationReason;

    private static final DateTimeFormatter FORMAT =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public JobDetails(Job job, int queuePosition,
                      LocalDateTime predictedStart,
                      LocalDateTime predictedFinish,
                      long remainingWait) {

        this.jobId = job.getJobId();
        this.studentId = job.getStudentId();
        this.title = job.getTitle();
        this.applicationName = job.getApplicationName();
        this.description = job.getDescription();
        this.taskSize = job.getTaskSize().toString();
        this.status = job.getStatus().toString();

        this.gpuId = job.getAssignedGpuId();
        this.duration = job.getEstimatedDuration();
        this.remainingRuntime = job.getRemainingRuntime();
        this.elapsedRuntime = job.getElapsedRuntime();
        this.queuePosition = queuePosition;

        this.submissionTime = job.getSubmissionTime();
        this.startTime = job.getStartTime();
        this.finishTime = job.getFinishTime();
        this.predictedStart = predictedStart;
        this.predictedFinish = predictedFinish;
        this.cancellationTime = job.getCancellationTime();

        this.remainingWait = remainingWait;
        this.cancelledBy = job.getCancelledBy();
        this.cancellationReason = job.getCancellationReason();

        if (job.getStatus() == JobStatus.COMPLETED) {
            allowanceCharged = duration;
        } else if (job.getStatus() == JobStatus.CANCELLED &&
                   job.getStartTime() != null) {
            allowanceCharged = elapsedRuntime;
        } else {
            allowanceCharged = 0;
        }
    }

    // Format dates; return N/A when unavailable
    private String formatTime(LocalDateTime time) {
        if (time == null) {
            return "N/A";
        }
        return time.format(FORMAT);
    }

    private String formatText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "N/A";
        }
        return text;
    }

    public int getJobId() {
        return jobId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getTitle() {
        return title;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskSize() {
        return taskSize;
    }

    public String getStatus() {
        return status;
    }

    public String getGpuId() {
        if (gpuId == -1) {
            return "N/A";
        }
        return String.valueOf(gpuId);
    }

    public int getDuration() {
        return duration;
    }

    public int getRemainingRuntime() {
        return remainingRuntime;
    }

    public int getElapsedRuntime() {
        return elapsedRuntime;
    }

    public int getAllowanceCharged() {
        return allowanceCharged;
    }

    public String getQueuePosition() {
        if (queuePosition == 0) {
            return "N/A";
        }
        return String.valueOf(queuePosition);
    }

    public String getSubmissionTime() {
        return formatTime(submissionTime);
    }

    public String getStartTime() {
        return formatTime(startTime);
    }

    public String getFinishTime() {
        return formatTime(finishTime);
    }

    public String getPredictedStart() {
        return formatTime(predictedStart);
    }

    public String getPredictedFinish() {
        return formatTime(predictedFinish);
    }

    public String getCancellationTime() {
        return formatTime(cancellationTime);
    }

    public String getCancelledBy() {
        return formatText(cancelledBy);
    }

    public String getCancellationReason() {
        return formatText(cancellationReason);
    }

    public long getRemainingWait() {
        return remainingWait;
    }

    public String getTotalQueueWait() {
        if (startTime == null) {
            return "N/A";
        }

        return String.valueOf(
            Duration.between(
                submissionTime, startTime
            ).toMinutes()
        );
    }

    @Override
    public String toString() {

        return "Job ID: " + jobId +
               "\nStudent ID: " + studentId +
               "\nTitle: " + title +
               "\nApplication: " + applicationName +
               "\nDescription: " + description +
               "\nTask Size: " + taskSize +
               "\nStatus: " + status +
               "\nGPU: " + getGpuId() +
               "\nDuration: " + duration +
               "\nRemaining Runtime: " + remainingRuntime +
               "\nElapsed Runtime: " + elapsedRuntime +
               "\nQueue Position: " + getQueuePosition() +
               "\nSubmission Time: " + getSubmissionTime() +
               "\nStart Time: " + getStartTime() +
               "\nFinish Time: " + getFinishTime() +
               "\nPredicted Start: " + getPredictedStart() +
               "\nPredicted Finish: " + getPredictedFinish() +
               "\nRemaining Wait: " + remainingWait +
               "\nTotal Queue Wait: " + getTotalQueueWait() +
               "\nAllowance Charged: " + allowanceCharged +
               "\nCancellation Time: " + getCancellationTime() +
               "\nCancelled By: " + getCancelledBy() +
               "\nCancellation Reason: " + getCancellationReason();
    }
}
