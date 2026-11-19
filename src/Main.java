
public class Main {
    public static void main(String[] args) {

        // Create administrator
        Admin admin = new Admin();

        // Create student (not approved yet)
        Student student = new Student(
            "10001",
            "pass123",
            "Ali",
            "ali@ku.ac.ae",
            "0501234567"
        );

        System.out.println("=== STUDENT REGISTERED ===");
        System.out.println(student);

        // Simulate administrator clicking Approve
        student.approve();

        System.out.println("\n=== STUDENT APPROVED ===");
        System.out.println(student);

        // Simulate student submitting a 30-minute job
        boolean accepted = student.reserveMinutes(30);

        System.out.println("\n=== JOB SUBMISSION ===");
        System.out.println("Job accepted: " + accepted);
        System.out.println("Available: " +
                           student.getAvailableMinutes());

        // Simulate job completion
        student.settleMinutes(30, 30);

        System.out.println("\n=== JOB COMPLETED ===");
        System.out.println("Used: " + student.getUsedMinutes());
        System.out.println("Reserved: " + student.getReservedMinutes());
        System.out.println("Available: " + student.getAvailableMinutes());
    }
}
