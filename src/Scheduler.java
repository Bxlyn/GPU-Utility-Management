import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;

public class Scheduler {

    // Attributes
    private GPU[] gpus;
    private ArrayList<Job> allJobs;
    private ArrayList<Job> waitingJobs;
    private LocalDateTime simulationTime;

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
}