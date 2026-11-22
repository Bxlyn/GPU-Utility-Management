package ui;

import service.GPUManagementSystem;
import storage.StorageManager;
import java.io.IOException;
import java.time.LocalDateTime;

/** A single shared application state for every Swing screen. */
public final class AppContext {
    private static GPUManagementSystem system;

    private AppContext() { }

    public static GPUManagementSystem get() {
        if (system == null) {
            throw new IllegalStateException("Application not initialized. Start using Main.java.");
        }
        return system;
    }

    public static void startNew(LocalDateTime initialTime) {
        if (initialTime == null) {
            throw new IllegalArgumentException("Select a simulation date and time.");
        }
        system = new GPUManagementSystem(initialTime);
    }

    // Replace the shared system with the saved one.
    public static void load() throws IOException, ClassNotFoundException {
        system = GPUManagementSystem.loadData();
    }

    public static void save() throws IOException {
        get().saveData();
    }

    public static boolean savedDataExists() {
        return StorageManager.saveExists();
    }
}
