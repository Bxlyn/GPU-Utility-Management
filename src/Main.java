
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        LocalDateTime clock =
            LocalDateTime.of(2026, 11, 20, 10, 0);

        // Create the scheduler
        Scheduler scheduler = new Scheduler(clock);

        // Create five jobs
        Job job1 = new Job(
            1, "10001", "Job 1",
            "Python", "Task 1",
            TaskSize.SMALL, clock
        );

        Job job2 = new Job(
            2, "10002", "Job 2",
            "TensorFlow", "Task 2",
            TaskSize.SMALL, clock
        );

        Job job3 = new Job(
            3, "10003", "Job 3",
            "MATLAB", "Task 3",
            TaskSize.SMALL, clock
        );

        Job job4 = new Job(
            4, "10004", "Job 4",
            "CUDA", "Task 4",
            TaskSize.SMALL, clock
        );

        Job job5 = new Job(
            5, "10005", "Job 5",
            "PyTorch", "Task 5",
            TaskSize.SMALL, clock
        );

        // Submit all five jobs
        scheduler.submitJob(job1);
        scheduler.submitJob(job2);
        scheduler.submitJob(job3);
        scheduler.submitJob(job4);
        scheduler.submitJob(job5);

        System.out.println("=== INITIAL GPU STATUS ===");
        scheduler.displayGpus();

        System.out.println("\nJob 5 Status: " +
                           job5.getStatus());

        System.out.println("Job 5 Queue Position: " +
                           scheduler.getQueuePosition(5));

        System.out.println("Job 5 Remaining Wait: " +
                           scheduler.getRemainingWait(5) +
                           " minutes");

        // Advance five simulated minutes
        for (int i = 0; i < 5; i++) {
            scheduler.advanceMinute();
        }

        System.out.println("\n=== AFTER 5 MINUTES ===");
        scheduler.displayGpus();

        System.out.println("\nJob 1 Status: " +
                           job1.getStatus());

        System.out.println("Job 5 Status: " +
                           job5.getStatus());

        System.out.println("Simulation Time: " +
                           scheduler.getSimulationTime());
    }
}
