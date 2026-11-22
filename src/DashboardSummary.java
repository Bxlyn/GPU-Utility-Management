
public class DashboardSummary {

    private int queuedJobs;
    private int runningJobs;
    private int completedJobs;
    private int cancelledJobs;

    private int busyGpus;
    private int availableGpus;

    private double utilization;

    public DashboardSummary(
            int queuedJobs,
            int runningJobs,
            int completedJobs,
            int cancelledJobs,
            int busyGpus,
            int availableGpus,
            double utilization) {

        this.queuedJobs = queuedJobs;
        this.runningJobs = runningJobs;
        this.completedJobs = completedJobs;
        this.cancelledJobs = cancelledJobs;
        this.busyGpus = busyGpus;
        this.availableGpus = availableGpus;
        this.utilization = utilization;
    }

    public int getQueuedJobs() {
        return queuedJobs;
    }

    public int getRunningJobs() {
        return runningJobs;
    }

    public int getCompletedJobs() {
        return completedJobs;
    }

    public int getCancelledJobs() {
        return cancelledJobs;
    }

    public int getBusyGpus() {
        return busyGpus;
    }

    public int getAvailableGpus() {
        return availableGpus;
    }

    public double getUtilization() {
        return utilization;
    }

    @Override
    public String toString() {

        String utilizationText;

        if (utilization < 0) {
            utilizationText = "N/A";
        } else {
            utilizationText =
                String.format("%.2f%%", utilization);
        }

        return "Queued Jobs: " + queuedJobs +
               "\nRunning Jobs: " + runningJobs +
               "\nCompleted Jobs: " + completedJobs +
               "\nCancelled Jobs: " + cancelledJobs +
               "\nBusy GPUs: " + busyGpus + "/4" +
               "\nAvailable GPUs: " + availableGpus +
               "\nGPU Utilization: " + utilizationText;
    }
}
