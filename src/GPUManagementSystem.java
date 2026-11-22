import java.io.Serializable;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class GPUManagementSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    // Attributes
    private Admin admin;
    private ArrayList<Student> students;
    private ArrayList<Student> pendingRequests;
    private ArrayList<Student> rejectedRequests;
    private transient HashSet<String> loggedInStudents;

    private Scheduler scheduler;
    private transient boolean adminLoggedIn;
    private int nextJobId;
    private ArrayList<TimeGrant> timeGrants;

    // Constructor
    public GPUManagementSystem(LocalDateTime startTime) {

        if (startTime == null) {
            throw new IllegalArgumentException(
                "Start time cannot be null"
            );
        }

        admin = new Admin();

        students = new ArrayList<>();
        pendingRequests = new ArrayList<>();
        rejectedRequests = new ArrayList<>();
        loggedInStudents = new HashSet<>();

        scheduler = new Scheduler(startTime);
        adminLoggedIn = false;
        nextJobId = 1;
        timeGrants = new ArrayList<>();
    }

    // Find an approved student by university ID
    public Student findStudent(String studentId) {

        for (Student student : students) {
            if (student.getUsername().equals(studentId)) {
                return student;
            }
        }

        return null;
    }

    // Find a pending registration request
    public Student findPendingRequest(String studentId) {

        for (Student student : pendingRequests) {
            if (student.getUsername().equals(studentId)) {
                return student;
            }
        }

        return null;
    }

    // Check if university ID is already registered
    private boolean idExists(String studentId) {

        if (findStudent(studentId) != null ||
            findPendingRequest(studentId) != null) {
            return true;
        }

        for (Student student : rejectedRequests) {
            if (student.getUsername().equals(studentId)) {
                return true;
            }
        }

        return false;
    }

    // Student registration
    public void registerStudent(
            String studentId,
            String password,
            String name,
            String email,
            String phone) {
        

        validateRegistration(studentId, password, name, email, phone);
        
        if (studentId == null || studentId.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "All registration fields are required"
            );
        }

        if (idExists(studentId)) {
            throw new IllegalArgumentException(
                "University ID already exists"
            );
        }

        Student student = new Student(
            studentId, password, name, email, phone
        );

        pendingRequests.add(student);
    }

    // Administrator login
    public boolean loginAdmin(String username, String password) {

        if (username == null || password == null) {
            return false;
        }

        boolean valid =
            admin.getUsername().equals(username) &&
            admin.checkPassword(password);

        if (valid) {
            adminLoggedIn = true;
        }

        return valid;
    }

    // Administrator logout
    public void logoutAdmin() {
        adminLoggedIn = false;
    }

    // Check administrator permission
    private void requireAdmin() {
        if (!adminLoggedIn) {
            throw new IllegalStateException(
                "Administrator login required"
            );
        }
    }

    // Approve a registration request
    public void approveStudent(String studentId) {

        requireAdmin();

        Student student = findPendingRequest(studentId);

        if (student == null) {
            throw new IllegalArgumentException(
                "Pending registration not found"
            );
        }

        student.approve();

        pendingRequests.remove(student);
        students.add(student);
    }

    // Reject a registration request
    public void rejectStudent(String studentId) {

        requireAdmin();

        Student student = findPendingRequest(studentId);

        if (student == null) {
            throw new IllegalArgumentException(
                "Pending registration not found"
            );
        }

        pendingRequests.remove(student);
        rejectedRequests.add(student);
    }

    // Student login
    public boolean loginStudent(String studentId,
                                String password) {

        Student student = findStudent(studentId);

        if (student == null || password == null) {
            return false;
        }

        boolean valid = student.isApproved() &&
                        student.checkPassword(password);

        if (valid) {
            loggedInStudents.add(studentId);
        }

        return valid;
    }

    // Student logout
    public void logoutStudent(String studentId) {
        loggedInStudents.remove(studentId);
    }

    // Check student permission
    private Student requireStudent(String studentId) {

        Student student = findStudent(studentId);

        if (student == null ||
            !loggedInStudents.contains(studentId)) {
            throw new IllegalStateException(
                "Approved student login required"
            );
        }

        return student;
    }

    // Grant additional GPU minutes
    public void grantExtraTime(String studentId,
                            int minutes, String reason) {

        requireAdmin();

        if (minutes <= 0 ||
            reason == null || reason.trim().isEmpty()) {

            throw new IllegalArgumentException(
                "Positive minutes and a reason are required"
            );
        }

        Student student = findStudent(studentId);

        if (student == null) {
            throw new IllegalArgumentException(
                "Student not found"
            );
        }

        student.grantMinutes(minutes);

        TimeGrant grant = new TimeGrant(
            studentId,
            minutes,
            reason,
            scheduler.getSimulationTime(),
            admin.getUsername()
        );

        timeGrants.add(grant);
    }

    // Adjust total allocated minutes
    public void adjustAllowance(
            String studentId, int totalMinutes) {

        requireAdmin();

        Student student = findStudent(studentId);

        if (student == null) {
            throw new IllegalArgumentException(
                "Student not found"
            );
        }

        student.setAllocatedMinutes(totalMinutes);
    }

    // Submit a GPU job
    public Job submitJob(
            String studentId,
            String title,
            String applicationName,
            String description,
            TaskSize taskSize) {

        Student student = requireStudent(studentId);

        if (taskSize == null) {
            throw new IllegalArgumentException(
                "Task size cannot be null"
            );
        }

        int duration = taskSize.getDuration();

        if (duration > student.getAvailableMinutes()) {
            throw new IllegalArgumentException(
                "Insufficient GPU minutes"
            );
        }

        // Create job using current simulation time
        Job job = new Job(
            nextJobId,
            studentId,
            title,
            applicationName,
            description,
            taskSize,
            scheduler.getSimulationTime()
        );

        // Reserve the full job duration
        if (!student.reserveMinutes(duration)) {
            throw new IllegalStateException(
                "Unable to reserve GPU minutes"
            );
        }

        try {
            scheduler.submitJob(job);
        } catch (RuntimeException e) {
            // Undo reservation if submission fails
            student.settleMinutes(duration, 0);
            throw e;
        }

        nextJobId++;

        return job;
    }

    // Advance simulation by one minute
    public void advanceMinute() {

        requireAdmin();

        ArrayList<Job> completedJobs =
                scheduler.advanceMinute();

        // Charge each completed job exactly once
        for (Job job : completedJobs) {

            Student student =
                    findStudent(job.getStudentId());

            if (student == null) {
                throw new IllegalStateException(
                    "Job owner not found"
                );
            }

            student.settleMinutes(
                job.getEstimatedDuration(),
                job.getEstimatedDuration()
            );
        }
    }

    // Shared cancellation logic
    private void cancelJobInternal(
            int jobId, String cancelledBy,
            String reason) {

        Job job = scheduler.findJob(jobId);

        if (job == null) {
            throw new IllegalArgumentException(
                "Job not found"
            );
        }

        Student student =
                findStudent(job.getStudentId());

        if (student == null) {
            throw new IllegalStateException(
                "Job owner not found"
            );
        }

        if (job.getStatus() != JobStatus.QUEUED &&
            job.getStatus() != JobStatus.RUNNING) {
            throw new IllegalStateException(
                "Only queued or running jobs can be cancelled"
            );
        }

        int charged = 0;

        if (job.getStatus() == JobStatus.RUNNING) {
            charged = job.getElapsedRuntime();
        }

        scheduler.cancelJob(
            jobId, cancelledBy, reason
        );

        // Release full reservation and charge
        // only the actual executed minutes
        student.settleMinutes(
            job.getEstimatedDuration(), charged
        );
    }

    // Student cancels their own job
    public void cancelStudentJob(
            String studentId, int jobId,
            String reason) {

        requireStudent(studentId);

        Job job = scheduler.findJob(jobId);

        if (job == null ||
            !job.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException(
                "Job not found or does not belong to student"
            );
        }

        cancelJobInternal(jobId, studentId, reason);
    }

    // Administrator cancels any job
    public void cancelAdminJob(
            int jobId, String reason) {

        requireAdmin();

        cancelJobInternal(
            jobId, admin.getUsername(), reason
        );
    }

    // Student views their own jobs
    public ArrayList<Job> getStudentJobs(String studentId) {

        requireStudent(studentId);

        ArrayList<Job> result = new ArrayList<>();

        for (Job job : scheduler.getAllJobs()) {
            if (job.getStudentId().equals(studentId)) {
                result.add(job);
            }
        }

        return result;
    }


    public ArrayList<JobDetails> getStudentJobDetails(
            String studentId) {

        requireStudent(studentId);

        ArrayList<JobDetails> result = new ArrayList<>();

        for (Job job : scheduler.getAllJobs()) {

            if (job.getStudentId().equals(studentId)) {

                int queuePosition =
                    scheduler.getQueuePosition(job.getJobId());

                LocalDateTime predictedStart =
                    scheduler.getPredictedStart(job.getJobId());

                LocalDateTime predictedFinish =
                    scheduler.getPredictedFinish(job.getJobId());

                long remainingWait =
                    scheduler.getRemainingWait(job.getJobId());

                JobDetails details = new JobDetails(
                    job,
                    queuePosition,
                    predictedStart,
                    predictedFinish,
                    remainingWait
                );

                result.add(details);
            }
        }

        return result;
    }


public ArrayList<JobDetails> getAllJobDetails() {

    requireAdmin();

    ArrayList<JobDetails> result = new ArrayList<>();

    for (Job job : scheduler.getAllJobs()) {

        int jobId = job.getJobId();

        JobDetails details = new JobDetails(
            job,
            scheduler.getQueuePosition(jobId),
            scheduler.getPredictedStart(jobId),
            scheduler.getPredictedFinish(jobId),
            scheduler.getRemainingWait(jobId)
        );

        result.add(details);
    }

    return result;
}



    // Student views their allowance
    public int getAvailableMinutes(String studentId) {
        return requireStudent(studentId)
                .getAvailableMinutes();
    }

    // Administrator views approved students
    public ArrayList<Student> getStudents() {
        requireAdmin();
        return new ArrayList<>(students);
    }

    // Administrator views pending requests
    public ArrayList<Student> getPendingRequests() {
        requireAdmin();
        return new ArrayList<>(pendingRequests);
    }

    // Administrator views rejected requests
    public ArrayList<Student> getRejectedRequests() {
        requireAdmin();
        return new ArrayList<>(rejectedRequests);
    }

    // Administrator views all jobs
    public ArrayList<Job> getAllJobs() {
        requireAdmin();
        return scheduler.getAllJobs();
    }

    // Administrator views GPUs
    public GPU[] getGpus() {
        requireAdmin();
        return scheduler.getGpus();
    }

    // Current simulation time
    public LocalDateTime getSimulationTime() {
        return scheduler.getSimulationTime();
    }

    public ArrayList<TimeGrant> getTimeGrants(String studentId) {

        requireAdmin();

        ArrayList<TimeGrant> result = new ArrayList<>();

        for (TimeGrant grant : timeGrants) {

            if (grant.getStudentId().equals(studentId)) {
                result.add(grant);
            }
        }

        return result;
    }


    // Get queue position (administrator)
    public int getQueuePosition(int jobId) {
        requireAdmin();
        return scheduler.getQueuePosition(jobId);
    }

    // Get predicted start (administrator)
    public LocalDateTime getPredictedStart(int jobId) {
        requireAdmin();
        return scheduler.getPredictedStart(jobId);
    }

    // Get predicted finish (administrator)
    public LocalDateTime getPredictedFinish(int jobId) {
        requireAdmin();
        return scheduler.getPredictedFinish(jobId);
    }

    // Get remaining wait (administrator)
    public long getRemainingWait(int jobId) {
        requireAdmin();
        return scheduler.getRemainingWait(jobId);
    }

    public int getStudentAllocatedMinutes(String studentId) {
        return requireStudent(studentId).getAllocatedMinutes();
    }

    public int getStudentUsedMinutes(String studentId) {
        return requireStudent(studentId).getUsedMinutes();
    }

    public int getStudentReservedMinutes(String studentId) {
        return requireStudent(studentId).getReservedMinutes();
    }


    /* DASHBOARD INFORMATION DISPLAY */

    public DashboardSummary getDashboardSummary() {

        requireAdmin();

        int queued = 0;
        int running = 0;
        int completed = 0;
        int cancelled = 0;

        for (Job job : scheduler.getAllJobs()) {

            if (job.getStatus() == JobStatus.QUEUED) {
                queued++;
            } else if (job.getStatus() == JobStatus.RUNNING) {
                running++;
            } else if (job.getStatus() == JobStatus.COMPLETED) {
                completed++;
            } else if (job.getStatus() == JobStatus.CANCELLED) {
                cancelled++;
            }
        }

        return new DashboardSummary(
            queued,
            running,
            completed,
            cancelled,
            scheduler.getBusyGpuCount(),
            scheduler.getAvailableGpuCount(),
            scheduler.getUtilization()
        );
    }


    public LocalDateTime getGpuExpectedFinish(int gpuId) {

        requireAdmin();

        for (GPU gpu : scheduler.getGpus()) {

            if (gpu.getGpuId() == gpuId) {

                if (gpu.isAvailable()) {
                    return null;
                }

                return scheduler.getSimulationTime()
                        .plusMinutes(
                            gpu.getRemainingRuntime()
                        );
            }
        }

        throw new IllegalArgumentException(
            "GPU not found"
        );
    }


    /* DATA RETRIEVAL PROCESS */

    private void readObject(java.io.ObjectInputStream input)
            throws IOException, ClassNotFoundException {

        input.defaultReadObject();

        // Everyone must log in again after restarting
        loggedInStudents = new HashSet<>();
        adminLoggedIn = false;
    }


    // Save current application data
    public void saveData() throws IOException {
        StorageManager.saveData(this);
    }

    // Load saved application data
    public static GPUManagementSystem loadData()
            throws IOException, ClassNotFoundException {

        return StorageManager.loadData();
    }

    /* DATA VALIDATION */

    private void validateRegistration(
            String studentId,
            String password,
            String name,
            String email,
            String phone) {

        if (studentId == null ||
            !studentId.matches("[0-9]+")) {
            throw new IllegalArgumentException(
                "University ID must contain digits only"
            );
        }

        if (password == null ||
            password.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Password cannot be empty"
            );
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Name cannot be empty"
            );
        }

        if (email == null ||
            !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException(
                "Invalid email address"
            );
        }

        if (phone == null ||
            !phone.matches("[+]?[0-9]{7,15}")) {
            throw new IllegalArgumentException(
                "Invalid phone number"
            );
        }
    }

}