import java.util.ArrayList;
import java.util.List;

/**
 * TaskQueue.java
 * 
 * Custom Data Structure: FIFO Queue (First-In, First-Out).
 * 
 * Used for normal pending tasks in the order they were scheduled.
 * Tasks are added at the rear and removed from the front.
 */
public class TaskQueue {

    // Node class representing an element in the queue
    private static class Node {
        Task task;
        Node next;

        Node(Task task) {
            this.task = task;
            this.next = null;
        }
    }

    private Node front; // Points to the front (head) of the queue
    private Node rear;  // Points to the end (tail) of the queue
    private int size;   // Number of elements in the queue

    public TaskQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    /**
     * Adds a task to the back (rear) of the queue.
     */
    public void enqueue(Task task) {
        if (task == null) return;

        Node newNode = new Node(task);
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the task at the front of the queue.
     */
    public Task dequeue() {
        if (isEmpty()) {
            return null;
        }

        Task removedTask = front.task;
        front = front.next;
        size--;

        if (front == null) {
            rear = null;
        }

        return removedTask;
    }

    /**
     * Returns the task at the front of the queue without removing it.
     */
    public Task peek() {
        if (isEmpty()) {
            return null;
        }
        return front.task;
    }

    /**
     * Checks if the queue is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of tasks in the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Removes a specific task by ID if it exists in the queue.
     */
    public boolean removeById(int taskId) {
        if (isEmpty()) return false;

        // If head node is the target
        if (front.task.getId() == taskId) {
            dequeue();
            return true;
        }

        Node current = front;
        while (current.next != null) {
            if (current.next.task.getId() == taskId) {
                // If the target is at rear, update rear
                if (current.next == rear) {
                    rear = current;
                }
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Returns all tasks in the queue as a List (from front to rear).
     */
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        Node current = front;
        while (current != null) {
            list.add(current.task);
            current = current.next;
        }
        return list;
    }

    /**
     * Displays queue contents in FIFO arrow notation: Front -> Task A -> Task B -> Rear
     */
    public String display() {
        if (isEmpty()) return "(Empty)";

        StringBuilder sb = new StringBuilder();
        Node current = front;
        while (current != null) {
            sb.append(current.task.getTitle());
            if (current.next != null) {
                sb.append(" -> ");
            }
            current = current.next;
        }
        return sb.toString();
    }
}
