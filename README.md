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

## How to Run

Open a terminal in the **project root folder**.

Compile:

```powershell
javac -d bin src/Main.java src/model/*.java src/service/*.java src/dto/*.java src/storage/*.java src/ui/*.java
```

Run:

```powershell
java -cp bin Main
```

## Default Administrator Login

- **Username:** `admin`
- **Password:** `gpu2026`

## Notes

The application simulates GPU execution rather than using physical GPUs. Student accounts require administrator approval before submitting jobs. Simulation time advances manually, and application data can be saved and restored.