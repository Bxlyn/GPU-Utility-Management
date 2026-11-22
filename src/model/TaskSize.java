package model;
import java.io.Serializable;
import java.io.ObjectStreamException;

// Allowed task sizes and their simulated durations.
public class TaskSize implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int duration;

    public static final TaskSize SMALL = new TaskSize("Small", 5);
    public static final TaskSize MEDIUM = new TaskSize("Medium", 15);
    public static final TaskSize LARGE = new TaskSize("Large", 30);
    public static final TaskSize VERY_LARGE = new TaskSize("Very Large", 60);

    private TaskSize(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    // Reuse the predefined size objects when loading saved data.
    private Object readResolve() throws ObjectStreamException {
        if ("Small".equals(name) && duration == 5) {
            return SMALL;
        }
        if ("Medium".equals(name) && duration == 15) {
            return MEDIUM;
        }
        if ("Large".equals(name) && duration == 30) {
            return LARGE;
        }
        if ("Very Large".equals(name) && duration == 60) {
            return VERY_LARGE;
        }

        throw new java.io.InvalidObjectException(
            "Unknown task size"
        );
    }


    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return name;
    }
}
