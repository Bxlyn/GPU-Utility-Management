
public class GPU {

    // Attributes
    private int gpuId;
    private Job currentJob;

    

    // Getters
    public int getGpuId() {
        return gpuId;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    // Check whether GPU is available
    public boolean isAvailable() {
        return currentJob == null;
    }

    // Assign a job to this GPU
    public void assignJob(Job job) {

        if (job == null) {
            throw new IllegalArgumentException(
                "Job cannot be null"
            );
        }

        if (!isAvailable()) {
            throw new IllegalStateException(
                "GPU " + gpuId + " is already busy"
            );
        }

        if (job.getStatus() != JobStatus.QUEUED) {
            throw new IllegalStateException(
                "Only queued jobs can be assigned"
            );
        }

        currentJob = job;
    }

    // Release the current job
    public void releaseJob() {

        if (isAvailable()) {
            throw new IllegalStateException(
                "GPU " + gpuId + " is already available"
            );
        }

        if (!currentJob.isFinished()) {
            throw new IllegalStateException(
                "Cannot release a running job"
            );
        }

        currentJob = null;
    }

    // Get remaining runtime
    public int getRemainingRuntime() {

        if (isAvailable()) {
            return 0;
        }

        return currentJob.getRemainingRuntime();
    }

    // Display GPU information
    @Override
    public String toString() {

        if (isAvailable()) {
            return "GPU " + gpuId + ": Available";
        }

        return "GPU " + gpuId +
               ": Busy" +
               "\nCurrent Job: " + currentJob.getJobId() +
               "\nRemaining Runtime: " +
               currentJob.getRemainingRuntime() + " minutes";
    }
}
