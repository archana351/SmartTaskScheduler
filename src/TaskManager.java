import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskManager.java
 * 
 * Core coordinator of the application.
 * Manages all tasks and synchronizes the underlying Data Structures:
 * 1. TaskPriorityQueue (Heap) - urgent task scheduling
 * 2. TaskQueue (FIFO) - pending queue order
 * 3. UndoStack (LIFO) - undo history
 * 4. TaskLinkedList - completed tasks history
 * 5. HashMap<Integer, Task> - O(1) fast ID lookups
 * 6. Bubble Sort - manual in-place sorting
 */
public class TaskManager {

    // Main in-memory task registry (Fast O(1) ID lookup)
    private final HashMap<Integer, Task> taskMap;

    // Preserved list of all tasks in display order
    private final List<Task> taskList;

    // Custom Data Structures
    private final TaskPriorityQueue priorityQueue;
    private final TaskQueue taskQueue;
    private final UndoStack undoStack;
    private final TaskLinkedList completedList;

    // ID counter starting from 101
    private int nextId;

    public TaskManager() {
        this.taskMap = new HashMap<>();
        this.taskList = new ArrayList<>();
        this.priorityQueue = new TaskPriorityQueue();
        this.taskQueue = new TaskQueue();
        this.undoStack = new UndoStack();
        this.completedList = new TaskLinkedList();
        this.nextId = 101;
    }

    /**
     * Initializes default sample tasks for demonstration.
     */
    public void loadSampleTasks() {
        addTaskInternal("Study Java", "Review OOP concepts and Swing UI", "HIGH", "15-09-2026", false);
        addTaskInternal("Practice DSA", "Solve Binary Heap and Stack problems", "HIGH", "14-09-2026", false);
        addTaskInternal("Complete DBMS Assignment", "Write SQL normalization queries", "MEDIUM", "18-09-2026", false);
        addTaskInternal("Study Computer Networks", "Read TCP/IP and OSI layer notes", "LOW", "20-09-2026", false);
        addTaskInternal("Practice SQL", "Complete LeetCode database exercises", "MEDIUM", "16-09-2026", false);
        addTaskInternal("Work on Project", "Implement Smart Task Scheduler in Java", "HIGH", "13-09-2026", false);
        addTaskInternal("Prepare Resume", "Update technical skills and projects", "LOW", "25-09-2026", false);
    }

    /**
     * Internal helper to create and register a task.
     * If recordUndo is true, pushes ADD action to UndoStack.
     */
    private Task addTaskInternal(String title, String description, String priority, String deadline, boolean recordUndo) {
        int id = nextId++;
        Task task = new Task(id, title, description, priority, deadline);

        // 1. Store in HashMap for O(1) direct lookup by ID
        taskMap.put(id, task);

        // 2. Keep in task list for display
        taskList.add(task);

        // 3. Add to Priority Queue (Max-Heap) for smart recommendations
        priorityQueue.add(task);

        // 4. Add to FIFO Queue for sequential scheduling
        taskQueue.enqueue(task);

        // 5. If user action, record on UndoStack
        if (recordUndo) {
            undoStack.push(new UndoStack.Action(UndoStack.Action.ActionType.ADD, task));
        }

        return task;
    }

    /**
     * Adds a new task triggered by user.
     */
    public Task addTask(String title, String description, String priority, String deadline) {
        return addTaskInternal(title, description, priority, deadline, true);
    }

    /**
     * Deletes a task by ID.
     * Records DELETE action in UndoStack for restoring.
     */
    public boolean deleteTask(int id) {
        Task task = taskMap.get(id);
        if (task == null) {
            return false;
        }

        // Record to UndoStack before removing
        undoStack.push(new UndoStack.Action(UndoStack.Action.ActionType.DELETE, task));

        // Remove from HashMap
        taskMap.remove(id);

        // Remove from display list
        taskList.remove(task);

        // Remove from custom data structures
        priorityQueue.removeById(id);
        taskQueue.removeById(id);
        completedList.remove(id);

        return true;
    }

    /**
     * Marks a task as completed.
     * Records COMPLETE action in UndoStack.
     */
    public boolean completeTask(int id) {
        Task task = taskMap.get(id);
        if (task == null || task.isCompleted()) {
            return false;
        }

        task.setCompleted(true);

        // Remove from active priority queue and FIFO queue
        priorityQueue.removeById(id);
        taskQueue.removeById(id);

        // Add to completed history linked list
        completedList.add(task);

        // Record to UndoStack
        undoStack.push(new UndoStack.Action(UndoStack.Action.ActionType.COMPLETE, task));

        return true;
    }

    /**
     * Undoes the last user action (ADD, DELETE, COMPLETE) using UndoStack.
     */
    public String undo() {
        if (undoStack.isEmpty()) {
            return "Nothing to undo.";
        }

        UndoStack.Action action = undoStack.pop();
        Task task = action.getTask();

        switch (action.getType()) {
            case ADD:
                // Undo ADD -> Remove the task
                taskMap.remove(task.getId());
                taskList.remove(task);
                priorityQueue.removeById(task.getId());
                taskQueue.removeById(task.getId());
                return "Undone: Added task \"" + task.getTitle() + "\" (ID: " + task.getId() + ") was removed.";

            case DELETE:
                // Undo DELETE -> Restore the task
                taskMap.put(task.getId(), task);
                taskList.add(task);
                if (!task.isCompleted()) {
                    priorityQueue.add(task);
                    taskQueue.enqueue(task);
                } else {
                    completedList.add(task);
                }
                return "Undone: Deleted task \"" + task.getTitle() + "\" (ID: " + task.getId() + ") was restored.";

            case COMPLETE:
                // Undo COMPLETE -> Mark back as Pending
                task.setCompleted(false);
                completedList.remove(task.getId());
                priorityQueue.add(task);
                taskQueue.enqueue(task);
                return "Undone: Completion of task \"" + task.getTitle() + "\" (ID: " + task.getId() + ") was reversed.";

            default:
                return "Unknown action type.";
        }
    }

    /**
     * Returns the next recommended task using Priority Queue logic:
     * - Only considers incomplete tasks.
     * - HIGH > MEDIUM > LOW.
     * - Earlier deadline wins ties.
     */
    public Task getNextTask() {
        // Find highest priority incomplete task from priorityQueue
        while (!priorityQueue.isEmpty()) {
            Task top = priorityQueue.peek();
            if (top != null && !top.isCompleted() && taskMap.containsKey(top.getId())) {
                return top;
            }
            // If already completed or deleted, discard it
            priorityQueue.remove();
        }

        // Fallback: search taskList in case priority queue needed rebuilding
        Task best = null;
        for (Task t : taskList) {
            if (!t.isCompleted()) {
                if (best == null) {
                    best = t;
                } else {
                    int p1 = t.getPriorityValue();
                    int p2 = best.getPriorityValue();
                    if (p1 > p2) {
                        best = t;
                    } else if (p1 == p2 && Task.compareDeadlines(t.getDeadline(), best.getDeadline()) < 0) {
                        best = t;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Searches for tasks by ID (via HashMap O(1)) or Title (substring match).
     */
    public List<Task> searchTasks(String query) {
        List<Task> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(taskList);
        }

        String trimmed = query.trim();

        // 1. Try numeric ID search using HashMap for O(1) lookup
        try {
            int searchId = Integer.parseInt(trimmed);
            Task match = taskMap.get(searchId);
            if (match != null) {
                results.add(match);
                return results;
            }
        } catch (NumberFormatException ignored) {
            // Not a number, proceed to title search
        }

        // 2. Search by title (case-insensitive substring)
        String lowerQuery = trimmed.toLowerCase();
        for (Task task : taskList) {
            if (task.getTitle().toLowerCase().contains(lowerQuery)) {
                results.add(task);
            }
        }

        return results;
    }

    /**
     * Sorts tasks using Bubble Sort algorithm.
     * Criteria options: "Priority", "Deadline", "Title".
     */
    public void sortTasks(String criteria) {
        int n = taskList.size();
        if (n <= 1) return;

        // Classic Bubble Sort implementation
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                Task t1 = taskList.get(j);
                Task t2 = taskList.get(j + 1);

                if (shouldSwap(t1, t2, criteria)) {
                    // Swap adjacent elements
                    taskList.set(j, t2);
                    taskList.set(j + 1, t1);
                    swapped = true;
                }
            }

            // Optimization: If no elements were swapped, list is already sorted
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Helper method to determine if t1 and t2 should be swapped in Bubble Sort.
     */
    private boolean shouldSwap(Task t1, Task t2, String criteria) {
        if ("Priority".equalsIgnoreCase(criteria)) {
            // Sort by Priority: HIGH (3) > MEDIUM (2) > LOW (1)
            // If t1 has lower priority value than t2, swap them so higher priority is first
            if (t1.getPriorityValue() != t2.getPriorityValue()) {
                return t1.getPriorityValue() < t2.getPriorityValue();
            }
            // If tied in priority, sort by earlier deadline
            return Task.compareDeadlines(t1.getDeadline(), t2.getDeadline()) > 0;
        } else if ("Deadline".equalsIgnoreCase(criteria)) {
            // Earlier deadline comes first
            return Task.compareDeadlines(t1.getDeadline(), t2.getDeadline()) > 0;
        } else if ("Title".equalsIgnoreCase(criteria)) {
            // Alphabetical A-Z (case-insensitive)
            return t1.getTitle().compareToIgnoreCase(t2.getTitle()) > 0;
        }
        return false;
    }

    /**
     * Returns all tasks in current display order.
     */
    public List<Task> getAllTasks() {
        return taskList;
    }

    /**
     * Looks up task by ID using HashMap.
     */
    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    /**
     * Generates a readable text summary of all DSA contents for demonstration.
     */
    public String getDsaStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("                 DSA STATUS OVERVIEW                \n");
        sb.append("====================================================\n\n");

        // 1. Priority Queue
        sb.append("1. PRIORITY QUEUE (Binary Max-Heap, Total: ").append(priorityQueue.size()).append("):\n");
        List<Task> pqTasks = priorityQueue.getSortedTasks();
        if (pqTasks.isEmpty()) {
            sb.append("   (Empty)\n");
        } else {
            for (int i = 0; i < pqTasks.size(); i++) {
                Task t = pqTasks.get(i);
                sb.append("   ").append(i + 1).append(". [")
                  .append(t.getPriority()).append(" | ").append(t.getDeadline()).append("] ")
                  .append(t.getTitle()).append(" (ID: ").append(t.getId()).append(")\n");
            }
        }
        sb.append("\n");

        // 2. FIFO Queue
        sb.append("2. TASK QUEUE (FIFO Order, Total: ").append(taskQueue.size()).append("):\n");
        sb.append("   Front -> ").append(taskQueue.display()).append(" <- Rear\n\n");

        // 3. Undo Stack
        sb.append("3. UNDO STACK (LIFO Order, Total: ").append(undoStack.size()).append("):\n");
        List<UndoStack.Action> actions = undoStack.getAllActions();
        if (actions.isEmpty()) {
            sb.append("   (Empty)\n");
        } else {
            for (int i = 0; i < actions.size(); i++) {
                sb.append("   [Top - ").append(i).append("] ").append(actions.get(i)).append("\n");
            }
        }
        sb.append("\n");

        // 4. Linked List (Completed / History)
        sb.append("4. LINKED LIST (Completed History, Total: ").append(completedList.size()).append("):\n");
        sb.append("   Head -> ").append(completedList.display()).append("\n\n");

        // 5. HashMap
        sb.append("5. HASHMAP (O(1) Lookup by ID, Total: ").append(taskMap.size()).append("):\n");
        if (taskMap.isEmpty()) {
            sb.append("   (Empty)\n");
        } else {
            for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
                sb.append("   Key: ").append(entry.getKey())
                  .append("  ==>  \"").append(entry.getValue().getTitle())
                  .append("\" [").append(entry.getValue().getPriority())
                  .append(", ").append(entry.getValue().isCompleted() ? "Completed" : "Pending").append("]\n");
            }
        }
        sb.append("\n====================================================");

        return sb.toString();
    }
}
