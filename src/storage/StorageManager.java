package storage;

import java.io.*;
import java.nio.file.*;

import service.GPUManagementSystem;

public class StorageManager {

    private static final String FILE_NAME = "gpu_data.dat";

    // Save the entire system
    public static void saveData(GPUManagementSystem system)
            throws IOException {

        if (system == null) {
            throw new IllegalArgumentException(
                "System cannot be null"
            );
        }

        Path destination = Paths.get(FILE_NAME);
        Path temporary = Paths.get(FILE_NAME + ".tmp");

        try {
            // Write the new data to a temporary file first
            try (ObjectOutputStream output =
                     new ObjectOutputStream(
                         new FileOutputStream(temporary.toFile()))) {

                output.writeObject(system);
            }

            // Replace the old file after saving succeeds
            try {
                Files.move(
                    temporary,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                    temporary,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    // Load the previously saved system
    public static GPUManagementSystem loadData()
            throws IOException, ClassNotFoundException {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            throw new FileNotFoundException(
                "No saved data found"
            );
        }

        try (ObjectInputStream input =
                 new ObjectInputStream(
                     new FileInputStream(file))) {

            Object data = input.readObject();

            if (!(data instanceof GPUManagementSystem)) {
                throw new IOException(
                    "Invalid saved system data"
                );
            }

            return (GPUManagementSystem) data;
        }
    }

    // Check if a save file exists
    public static boolean saveExists() {
        return new File(FILE_NAME).exists();
    }
}
