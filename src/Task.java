/**
 * Task.java
 * Represents a single task in the Smart Task Scheduler.
 * 
 * Contains task attributes:
 * - id: Unique identifier
 * - title: Title of the task
 * - description: Detailed task notes
 * - priority: "HIGH", "MEDIUM", or "LOW"
 * - deadline: Date string in "DD-MM-YYYY" format
 * - completed: Whether the task has been completed
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private String priority;
    private String deadline;
    private boolean completed;

    // Constructor
    public Task(int id, String title, String description, String priority, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority.toUpperCase();
        this.deadline = deadline;
        this.completed = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority.toUpperCase();
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getStatus() {
        return completed ? "Completed" : "Pending";
    }

    /**
     * Converts priority string to numeric value for comparison.
     * HIGH = 3, MEDIUM = 2, LOW = 1
     */
    public int getPriorityValue() {
        switch (priority.toUpperCase()) {
            case "HIGH":
                return 3;
            case "MEDIUM":
                return 2;
            case "LOW":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Compares two deadline strings formatted as "DD-MM-YYYY".
     * Returns:
     *   negative if d1 is earlier than d2,
     *   0 if equal,
     *   positive if d1 is later than d2.
     */
    public static int compareDeadlines(String d1, String d2) {
        try {
            String[] p1 = d1.trim().split("[-/.]");
            String[] p2 = d2.trim().split("[-/.]");
            if (p1.length >= 3 && p2.length >= 3) {
                int day1 = Integer.parseInt(p1[0].trim());
                int month1 = Integer.parseInt(p1[1].trim());
                int year1 = Integer.parseInt(p1[2].trim());

                int day2 = Integer.parseInt(p2[0].trim());
                int month2 = Integer.parseInt(p2[1].trim());
                int year2 = Integer.parseInt(p2[2].trim());

                if (year1 != year2) {
                    return Integer.compare(year1, year2);
                }
                if (month1 != month2) {
                    return Integer.compare(month1, month2);
                }
                return Integer.compare(day1, day2);
            }
        } catch (Exception ignored) {
            // Fallback to lexicographical comparison if parsing fails
        }
        return d1.compareToIgnoreCase(d2);
    }

    @Override
    public String toString() {
        return "ID: " + id + " | " + title + " (" + priority + ", " + deadline + ") [" + (completed ? "Completed" : "Pending") + "]";
    }
}
