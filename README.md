# GPU Utilization Management System

A Java application developed for the **COSC230 Object-Oriented Programming** course at Khalifa University.

The system simulates GPU resource management, allowing students to request GPU processing time and administrators to manage accounts, jobs, and resource allocation.

## Features

- Student registration and administrator approval.
- Student and administrator login.
- GPU job submission and cancellation.
- First Come First Served (FCFS) scheduling across four GPUs.
- Simulated clock with an Advance Minute feature.
- GPU time allowance tracking and utilization statistics.
- Job history, queue positions, and predicted waiting times.
- File-based saving and loading.

## Project Structure

```text
src/
├── Main.java       # Application entry point
├── model/          # User, Student, Job, GPU, etc.
├── service/        # Scheduling and application logic
├── dto/            # Dashboard and job information
├── storage/        # File saving and loading
└── ui/             # Java Swing interface
```

## Requirements

- Java JDK 17 or later
- Java Swing (included with the JDK)
- A terminal or Java IDE, such as Visual Studio Code

No additional external libraries are required.

## Setup and Execution Instructions

1. Clone the GitHub repository:

   ```bash
   git clone https://github.com/Bxlyn/GPU-Utility-Management.git
   ```

2. Open the downloaded project folder in VS Code or a terminal.

3. Make sure Java is installed:

   ```bash
   java -version
   javac -version
   ```

4. From the project root folder, compile the Java files:

   ```powershell
   javac -d bin src/Main.java src/model/*.java src/service/*.java src/dto/*.java src/storage/*.java src/ui/*.java
   ```

5. Run the application:

   ```powershell
   java -cp bin Main
   ```

6. When the application starts, choose whether to restore previously saved data or initialize a new simulation. For a new simulation, select the starting date and time.

7. Register a student account or log in using the administrator credentials.

## Default Administrator Login

- **Username:** `admin`
- **Password:** `gpu2026`

These credentials are predefined for this academic project.

## Sample Test Data

The following fictional student accounts can be used to test registration, approval, and GPU job management.

| University ID | Student Name | Email | Account Status |
|---|---|---|---|
| 10001 | Ali Hassan | ali@example.com | Approved |
| 10002 | Sara Ahmed | sara@example.com | Approved |
| 10003 | Omar Khalid | omar@example.com | Approved |
| 10004 | Noor Salem | noor@example.com | Approved |
| 10005 | Fatima Saeed | fatima@example.com | Approved |
| 10006 | Khalid Mansoor | khalid@example.com | Pending |
| 10007 | Maryam Rashid | maryam@example.com | Rejected |

These are suggested test records, not accounts automatically created by the application. Register them through the interface and set their statuses using the administrator account.

### Sample GPU Jobs

| Job Title | Application | Task Size | Duration |
|---|---|---|---|
| Image Classification | Python | Small | 5 minutes |
| Matrix Processing | MATLAB | Medium | 15 minutes |
| Neural Network Training | PyTorch | Large | 30 minutes |
| Data Analysis | TensorFlow | Small | 5 minutes |
| Simulation Task | Python | Medium | 15 minutes |

### Sample Test Procedure

1. Initialize the simulation clock to `22/11/2026 10:00`.
2. Register the fictional student accounts.
3. Log in as administrator and approve the first five students.
4. Log in as an approved student and submit the five sample jobs.
5. Verify that four jobs start running and the fifth remains queued.
6. Advance the simulation by five minutes.
7. Verify that completed jobs release their GPUs and waiting jobs begin execution.
8. Test queued and running job cancellation.
9. Save the simulation, restart the application, and restore the saved data.

## GPU Scheduling Rules

- The system manages four simulated NVIDIA H100 GPUs.
- Each GPU processes one job at a time.
- Jobs are scheduled using FCFS.
- Approved students receive 100 GPU minutes by default.
- Job durations are 5, 15, 30, or 60 minutes.
- The full duration is reserved when a job is accepted.
- Cancelling a queued job does not charge any minutes.
- Cancelling a running job charges only the elapsed execution time.
- The simulation clock advances manually using the Advance Minute button.

## Data Storage

The application saves its state locally in `gpu_data.dat` using Java object serialization.

Saved information includes student accounts, registration requests, GPU assignments, job records, time allowances, and simulation time.

The file is excluded from Git tracking because it may contain account credentials and other local application data.

## Notes

The application simulates GPU execution rather than performing computations on physical GPUs. Student accounts require administrator approval before submitting jobs.

Simulation time does not advance while the application is closed. The saved state can be restored when the application is restarted.

This project was developed for educational purposes as part of COSC230.