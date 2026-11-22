package backend;

public class TaskSize {

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
