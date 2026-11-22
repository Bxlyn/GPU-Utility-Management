package service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;

import model.GPU;
import model.Job;
import model.JobStatus;

import java.io.Serializable;

public class Scheduler implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes
    private GPU[] gpus;
    private ArrayList<Job> allJobs;
    private ArrayList<Job> waitingJobs;
    private LocalDateTime simulationTime;
    private long elapsedSimulationMinutes = 0;
    private long totalBusyGpuMinutes = 0;


    // Constructor
    public Scheduler(LocalDateTime startTime) {

        if (startTime == null) {
            throw new IllegalArgumentException(
                "Simulation time cannot be null"
            );
        }

        simulationTime = startTime;

        gpus = new GPU[4];

        for (int i = 0; i < 4; i++) {
            gpus[i] = new GPU(i + 1);
        }

        allJobs = new ArrayList<>();
        waitingJobs = new ArrayList<>();
    }

    // Get current simulation time
    public LocalDateTime getSimulationTime() {
        return simulationTime;
    }

    // Get all GPUs
    public GPU[] getGpus() {
        return gpus.clone();
    }

    // Get all jobs
    public ArrayList<Job> getAllJobs() {
        return new ArrayList<>(allJobs);
    }

    // Get waiting jobs
    public ArrayList<Job> getWaitingJobs() {
        return new ArrayList<>(waitingJobs);
    }

    // Submit a new job
    public void submitJob(Job job) {

        if (job == null) {
            throw new IllegalArgumentException(
                "Job cannot be null"
            );
        }

        if (job.getStatus() != JobStatus.QUEUED) {
            throw new IllegalArgumentException(
                "Only queued jobs can be submitted"
            );
        }

        if (!job.getSubmissionTime().equals(simulationTime)) {
            throw new IllegalArgumentException(
                "Job must use the current simulation time"
            );
        }

        // Prevent duplicate job IDs
        for (Job existingJob : allJobs) {
            if (existingJob.getJobId() == job.getJobId()) {
                throw new IllegalArgumentException(
                    "Job ID already exists"
                );
            }
        }

        allJobs.add(job);
        waitingJobs.add(job);

        // Sort jobs by submission time, then job ID
        waitingJobs.sort((a, b) -> {

            int comparison = a.getSubmissionTime()
                    .compareTo(b.getSubmissionTime());

            if (comparison != 0) {
                return comparison;
            }

            return Integer.compare(
                a.getJobId(), b.getJobId()
            );
        });

        // Assign jobs if GPUs are available
        dispatchJobs();
    }

    // Assign waiting jobs to available GPUs
    public void dispatchJobs() {

        for (GPU gpu : gpus) {

            if (waitingJobs.isEmpty()) {
                break;
            }

            if (gpu.isAvailable()) {

                Job job = waitingJobs.get(0);

                gpu.assignJob(job);
                job.start(gpu.getGpuId(), simulationTime);

                waitingJobs.remove(0);
            }
        }
    }


    public Job cancelJob(int jobId, String cancelledBy,
                        String reason) {

        Job job = findJob(jobId);

        if (job == null) {
            throw new IllegalArgumentException("Job not found");
        }

        if (job.getStatus() != JobStatus.QUEUED &&
            job.getStatus() != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Job cannot be cancelled"
            );
        }

        if (cancelledBy == null || cancelledBy.trim().isEmpty() ||
            reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Cancellation user and reason are required"
            );
        }

        if (job.getStatus() == JobStatus.QUEUED) {

            job.cancel(simulationTime, cancelledBy, reason);
            waitingJobs.remove(job);

        } else {

            GPU assignedGpu = null;

            for (GPU gpu : gpus) {
                if (gpu.getCurrentJob() == job) {
                    assignedGpu = gpu;
                    break;
                }
            }

            if (assignedGpu == null) {
                throw new IllegalStateException(
                    "Running job has no assigned GPU"
                );
            }

            job.cancel(simulationTime, cancelledBy, reason);
            assignedGpu.releaseJob();

            // Immediately start the next waiting job
            dispatchJobs();
        }

        return job;
    }


    // Advance simulation clock by one minute
    public ArrayList<Job> advanceMinute() {

        ArrayList<Job> completedJobs = new ArrayList<>();

        // Count GPUs that are busy before advancing
        int busyCount = 0;

        for (GPU gpu : gpus) {
            if (!gpu.isAvailable()) {
                busyCount++;
            }
        }

        totalBusyGpuMinutes += busyCount;
        elapsedSimulationMinutes++;

        // Advance the shared simulation clock
        simulationTime = simulationTime.plusMinutes(1);

        // Update running jobs
        for (GPU gpu : gpus) {

            if (!gpu.isAvailable()) {

                Job job = gpu.getCurrentJob();

                job.advanceOneMinute();

                if (job.getRemainingRuntime() == 0) {

                    job.complete(simulationTime);

                    completedJobs.add(job);

                    gpu.releaseJob();
                }
            }
        }

        // Start waiting jobs on available GPUs
        dispatchJobs();

        return completedJobs;
    }


    // Search for a job by ID
    public Job findJob(int jobId) {

        for (Job job : allJobs) {

            if (job.getJobId() == jobId) {
                return job;
            }
        }

        return null;
    }

    // Get queue position of a job
    public int getQueuePosition(int jobId) {

        for (int i = 0; i < waitingJobs.size(); i++) {

            if (waitingJobs.get(i).getJobId() == jobId) {
                return i + 1;
            }
        }

        return 0;
    }

    // Predict the start time of a queued job
    public LocalDateTime getPredictedStart(int jobId) {

        Job target = findJob(jobId);

        if (target == null) {
            throw new IllegalArgumentException(
                "Job not found"
            );
        }

        if (target.getStatus() == JobStatus.RUNNING ||
            target.getStatus() == JobStatus.COMPLETED) {
            return target.getStartTime();
        }

        if (target.getStatus() == JobStatus.CANCELLED) {
            return null;
        }

        // Copy each GPU's expected availability
        LocalDateTime[] availableTimes =
                new LocalDateTime[4];

        for (int i = 0; i < 4; i++) {

            if (gpus[i].isAvailable()) {
                availableTimes[i] = simulationTime;
            } else {
                availableTimes[i] = simulationTime.plusMinutes(
                    gpus[i].getRemainingRuntime()
                );
            }
        }

        // Simulate the waiting queue
        for (Job job : waitingJobs) {

            int earliestGpu = 0;

            for (int i = 1; i < 4; i++) {

                if (availableTimes[i].isBefore(
                        availableTimes[earliestGpu])) {
                    earliestGpu = i;
                }
            }

            LocalDateTime predictedStart =
                    availableTimes[earliestGpu];

            if (job.getJobId() == jobId) {
                return predictedStart;
            }

            availableTimes[earliestGpu] =
                predictedStart.plusMinutes(
                    job.getEstimatedDuration()
                );
        }

        return null;
    }

    // Predict finish time
    public LocalDateTime getPredictedFinish(int jobId) {

        Job job = findJob(jobId);

        if (job == null) {
            throw new IllegalArgumentException(
                "Job not found"
            );
        }

        if (job.getStatus() == JobStatus.COMPLETED ||
            job.getStatus() == JobStatus.CANCELLED) {
            return job.getFinishTime();
        }

        if (job.getStatus() == JobStatus.RUNNING) {
            return simulationTime.plusMinutes(
                job.getRemainingRuntime()
            );
        }

        LocalDateTime start =
                getPredictedStart(jobId);

        if (start == null) {
            return null;
        }

        return start.plusMinutes(
            job.getEstimatedDuration()
        );
    }

    // Calculate remaining waiting time
    public long getRemainingWait(int jobId) {

        Job job = findJob(jobId);

        if (job == null) {
            throw new IllegalArgumentException(
                "Job not found"
            );
        }

        if (job.getStatus() != JobStatus.QUEUED) {
            return 0;
        }

        LocalDateTime predictedStart =
                getPredictedStart(jobId);

        return Duration.between(
            simulationTime, predictedStart
        ).toMinutes();
    }


    public long getElapsedSimulationMinutes() {
        return elapsedSimulationMinutes;
    }

    public long getTotalBusyGpuMinutes() {
        return totalBusyGpuMinutes;
    }

    public double getUtilization() {

        if (elapsedSimulationMinutes == 0) {
            return -1;
        }

        return (totalBusyGpuMinutes * 100.0) /
            (4.0 * elapsedSimulationMinutes);
    }

    public int getBusyGpuCount() {

        int count = 0;

        for (GPU gpu : gpus) {
            if (!gpu.isAvailable()) {
                count++;
            }
        }

        return count;
    }

    public int getAvailableGpuCount() {
        return 4 - getBusyGpuCount();
    }


    // Display all GPU statuses
    public void displayGpus() {

        for (GPU gpu : gpus) {
            System.out.println(gpu);
        }
    }

    // Display all jobs
    public void displayJobs() {

        for (Job job : allJobs) {
            System.out.println(job);
            System.out.println("----------------");
        }
    }
}
