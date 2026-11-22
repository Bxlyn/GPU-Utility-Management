package model;
import java.io.Serializable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Job implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes
    private int jobId;
    private String studentId;
    private String title;
    private String applicationName;
    private String description;

    private TaskSize taskSize;
    private int workloadUnits;
    private int estimatedDuration;
    private int remainingRuntime;

    private JobStatus status;
    private int assignedGpuId;

    private LocalDateTime submissionTime;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancellationTime;

    private String cancelledBy;
    private String cancellationReason;

    // Constructor
    public Job(int jobId, String studentId, String title,
               String applicationName, String description,
               TaskSize taskSize, LocalDateTime submissionTime) {

        if (taskSize == null) {
            throw new IllegalArgumentException(
                "Task size cannot be null"
            );
        }

        if (submissionTime == null) {
            throw new IllegalArgumentException(
                "Submission time cannot be null"
            );
        }

        if (jobId <= 0) {
            throw new IllegalArgumentException(
                "Job ID must be positive"
            );
        }

        if (studentId == null || studentId.trim().isEmpty() ||
            title == null || title.trim().isEmpty() ||
            applicationName == null || applicationName.trim().isEmpty() ||
            description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Job details cannot be empty"
            );
        }

        this.jobId = jobId;
        this.studentId = studentId;
        this.title = title;
        this.applicationName = applicationName;
        this.description = description;

        this.taskSize = taskSize;
        this.workloadUnits = taskSize.getDuration();
        this.estimatedDuration = taskSize.getDuration();
        this.remainingRuntime = estimatedDuration;

        this.status = JobStatus.QUEUED;
        this.assignedGpuId = -1;
        this.submissionTime = submissionTime;
    }

    // Getters
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

    public TaskSize getTaskSize() {
        return taskSize;
    }

    public int getWorkloadUnits() {
        return workloadUnits;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public int getRemainingRuntime() {
        return remainingRuntime;
    }

    public int getElapsedRuntime() {
        return estimatedDuration - remainingRuntime;
    }

    public JobStatus getStatus() {
        return status;
    }

    public int getAssignedGpuId() {
        return assignedGpuId;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public LocalDateTime getCancellationTime() {
        return cancellationTime;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    // Start a queued job on a GPU
    public void start(int gpuId, LocalDateTime time) {

        if (status != JobStatus.QUEUED) {
            throw new IllegalStateException(
                "Only queued jobs can start"
            );
        }

        if (gpuId <= 0 || time == null ||
            time.isBefore(submissionTime)) {
            throw new IllegalArgumentException(
                "Invalid GPU ID or start time"
            );
        }

        assignedGpuId = gpuId;
        startTime = time;
        status = JobStatus.RUNNING;
    }

    // Advance the job by one simulated minute
    public void advanceOneMinute() {

        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Only running jobs can advance"
            );
        }

        if (remainingRuntime <= 0) {
            throw new IllegalStateException(
                "Job has no remaining runtime"
            );
        }

        remainingRuntime--;
    }

    // Complete a running job
    public void complete(LocalDateTime time) {

        if (status != JobStatus.RUNNING ||
            remainingRuntime != 0) {
            throw new IllegalStateException(
                "Job cannot be completed"
            );
        }

        if (time == null || time.isBefore(startTime)) {
            throw new IllegalArgumentException(
                "Invalid completion time"
            );
        }

        status = JobStatus.COMPLETED;
        finishTime = time;
    }

    // Cancel a queued or running job
    public void cancel(LocalDateTime time,
                       String cancelledBy,
                       String reason) {

        if (status != JobStatus.QUEUED &&
            status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Job cannot be cancelled"
            );
        }

        if (time == null ||
            time.isBefore(submissionTime) ||
            cancelledBy == null ||
            cancelledBy.trim().isEmpty() ||
            reason == null ||
            reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Invalid cancellation details"
            );
        }

        if (status == JobStatus.RUNNING) {
            if (time.isBefore(startTime)) {
                throw new IllegalArgumentException(
                    "Cancellation cannot precede start"
                );
            }

            finishTime = time;
        }

        cancellationTime = time;
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;

        status = JobStatus.CANCELLED;
    }

    // Calculate actual queue waiting time
    public long getWaitingMinutes() {

        if (startTime == null) {
            return 0;
        }

        return java.time.Duration.between(
            submissionTime, startTime
        ).toMinutes();
    }

    // Check whether the job is finished or cancelled
    public boolean isFinished() {
        return status == JobStatus.COMPLETED ||
               status == JobStatus.CANCELLED;
    }

    // Display job information
    
    @Override
    public String toString() {

        DateTimeFormatter format =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        return "Job ID: " + jobId +
            "\nStudent ID: " + studentId +
            "\nTitle: " + title +
            "\nApplication: " + applicationName +
            "\nDescription: " + description +
            "\nTask Size: " + taskSize +
            "\nWorkload Units: " + workloadUnits +
            "\nDuration: " + estimatedDuration +
            "\nRemaining Runtime: " + remainingRuntime +
            "\nStatus: " + status +
            "\nAssigned GPU: " +
                (assignedGpuId == -1 ? "N/A" : assignedGpuId) +
            "\nSubmission Time: " +
                submissionTime.format(format) +
            "\nStart Time: " +
                (startTime == null ? "N/A" :
                    startTime.format(format)) +
            "\nFinish Time: " +
                (finishTime == null ? "N/A" :
                    finishTime.format(format)) +
            "\nCancellation Time: " +
                (cancellationTime == null ? "N/A" :
                    cancellationTime.format(format)) +
            "\nCancelled By: " +
                (cancelledBy == null ? "N/A" : cancelledBy) +
            "\nCancellation Reason: " +
                (cancellationReason == null ?
                    "N/A" : cancellationReason);
    }

}
