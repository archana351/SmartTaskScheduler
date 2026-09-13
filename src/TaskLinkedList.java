import java.util.ArrayList;
import java.util.List;

/**
 * TaskLinkedList.java
 * 
 * Custom Data Structure: Singly Linked List.
 * 
 * Used to maintain completed tasks and task execution history.
 * Demonstrates node linking, traversal, insertion, and deletion.
 */
public class TaskLinkedList {

    // Node representing each element in the singly linked list
    public static class Node {
        public Task task;
        public Node next;

        public Node(Task task) {
            this.task = task;
            this.next = null;
        }
    }

    private Node head; // Points to first node
    private int size;  // Total number of elements

    public TaskLinkedList() {
        this.head = null;
        this.size = 0;
    }

    /**
     * Adds a task to the end of the linked list.
     */
    public void add(Task task) {
        if (task == null) return;

        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /**
     * Removes a task by its ID from the linked list.
     * Returns true if found and removed, false otherwise.
     */
    public boolean remove(int taskId) {
        if (head == null) return false;

        // If head node contains the task
        if (head.task.getId() == taskId) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (current.next.task.getId() == taskId) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Checks if a task with the given ID exists in the list.
     */
    public boolean contains(int taskId) {
        Node current = head;
        while (current != null) {
            if (current.task.getId() == taskId) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Returns the number of tasks in the linked list.
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the linked list is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clears all nodes from the linked list.
     */
    public void clear() {
        head = null;
        size = 0;
    }

    /**
     * Returns all tasks in the list as an ArrayList.
     */
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.task);
            current = current.next;
        }
        return list;
    }

    /**
     * Formats the linked list as a chain: Task A -> Task B -> Task C
     */
    public String display() {
        if (isEmpty()) return "(Empty)";

        StringBuilder sb = new StringBuilder();
        Node current = head;
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
