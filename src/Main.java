
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        try {
            // Create a new system
            GPUManagementSystem system =
                new GPUManagementSystem(
                    LocalDateTime.of(2026, 11, 22, 10, 0)
                );

            // Register student
            system.registerStudent(
                "10001",
                "pass123",
                "Ali",
                "ali@ku.ac.ae",
                "0501234567"
            );

            // Administrator approves student
            system.loginAdmin("admin", "gpu2026");
            system.approveStudent("10001");

            // Student logs in
            system.loginStudent("10001", "pass123");

            // Submit five small jobs
            for (int i = 1; i <= 5; i++) {
                system.submitJob(
                    "10001",
                    "Job " + i,
                    "Python",
                    "GPU task " + i,
                    TaskSize.SMALL
                );
            }

            // Advance two minutes
            system.advanceMinute();
            system.advanceMinute();

            System.out.println("=== BEFORE SAVING ===");

            printState(system);

            // Save data
            system.saveData();
            System.out.println("\nData saved successfully.");

            // Simulate restarting by loading from file
            system = GPUManagementSystem.loadData();

            System.out.println("\n=== AFTER LOADING ===");

            // Login again after loading
            system.loginAdmin("admin", "gpu2026");

            printState(system);

            // Continue the simulation
            for (int i = 0; i < 3; i++) {
                system.advanceMinute();
            }

            System.out.println("\n=== AFTER 3 MORE MINUTES ===");

            printState(system);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printState(
            GPUManagementSystem system) {

        System.out.println(
            "Time: " + system.getSimulationTime()
        );

        for (GPU gpu : system.getGpus()) {
            System.out.println(gpu);
        }

        for (Job job : system.getAllJobs()) {
            System.out.println(
                "Job " + job.getJobId() +
                ": " + job.getStatus()
            );
        }

        Student student = system.findStudent("10001");

        System.out.println(
            "Allocated: " + student.getAllocatedMinutes()
        );

        System.out.println(
            "Used: " + student.getUsedMinutes()
        );

        System.out.println(
            "Reserved: " + student.getReservedMinutes()
        );

        System.out.println(
            "Available: " + student.getAvailableMinutes()
        );
    }
}
