import java.util.ArrayList;
import java.util.List;

/**
 * TaskPriorityQueue.java
 * 
 * Custom Data Structure: Priority Queue using a Binary Max-Heap.
 * 
 * Determines which task is the most urgent:
 * 1. HIGH priority > MEDIUM priority > LOW priority.
 * 2. If priorities are equal, the task with the earlier deadline comes first.
 * 
 * Implemented manually without using java.util.PriorityQueue.
 */
public class TaskPriorityQueue {
    private Task[] heap;
    private int size;
    private static final int INITIAL_CAPACITY = 20;

    public TaskPriorityQueue() {
        this.heap = new Task[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Checks if task t1 has higher urgency/priority than task t2.
     * Rule:
     * - Higher priority rank (HIGH=3, MEDIUM=2, LOW=1) wins.
     * - If tied, earlier deadline wins.
     */
    private boolean hasHigherPriority(Task t1, Task t2) {
        if (t1 == null) return false;
        if (t2 == null) return true;

        int p1 = t1.getPriorityValue();
        int p2 = t2.getPriorityValue();

        if (p1 != p2) {
            return p1 > p2;
        }

        // Ties broken by earlier deadline
        return Task.compareDeadlines(t1.getDeadline(), t2.getDeadline()) < 0;
    }

    /**
     * Adds a task to the Priority Queue and restores heap property (Sift Up).
     */
    public void add(Task task) {
        if (task == null) return;

        // Resize array if capacity is reached
        if (size == heap.length) {
            resize();
        }

        // Place new task at the end of the heap
        heap[size] = task;
        siftUp(size);
        size++;
    }

    /**
     * Sifts up the element at the given index until heap property is satisfied.
     */
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (hasHigherPriority(heap[index], heap[parentIndex])) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Sifts down the element at the given index until heap property is satisfied.
     */
    private void siftDown(int index) {
        while (index < size) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int best = index;

            if (leftChild < size && hasHigherPriority(heap[leftChild], heap[best])) {
                best = leftChild;
            }
            if (rightChild < size && hasHigherPriority(heap[rightChild], heap[best])) {
                best = rightChild;
            }

            if (best != index) {
                swap(index, best);
                index = best;
            } else {
                break;
            }
        }
    }

    /**
     * Swaps two elements in the heap array.
     */
    private void swap(int i, int j) {
        Task temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    /**
     * Doubles the capacity of the internal array.
     */
    private void resize() {
        Task[] newHeap = new Task[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }

    /**
     * Returns the highest priority task without removing it.
     */
    public Task peek() {
        if (isEmpty()) {
            return null;
        }
        return heap[0];
    }

    /**
     * Removes and returns the highest priority task.
     */
    public Task remove() {
        if (isEmpty()) {
            return null;
        }
        Task root = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }
        return root;
    }

    /**
     * Removes a specific task by ID (useful when a task is deleted or completed).
     */
    public boolean removeById(int taskId) {
        for (int i = 0; i < size; i++) {
            if (heap[i] != null && heap[i].getId() == taskId) {
                heap[i] = heap[size - 1];
                heap[size - 1] = null;
                size--;
                if (i < size) {
                    siftDown(i);
                    siftUp(i);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the queue is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of tasks in the priority queue.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all tasks from the priority queue.
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    /**
     * Returns an ordered list of tasks for display / demonstration.
     * Extracts all elements into sorted order without mutating this queue.
     */
    public List<Task> getSortedTasks() {
        List<Task> list = new ArrayList<>();
        // Make a copy of current heap
        TaskPriorityQueue copy = new TaskPriorityQueue();
        for (int i = 0; i < size; i++) {
            copy.add(heap[i]);
        }
        while (!copy.isEmpty()) {
            list.add(copy.remove());
        }
        return list;
    }
}
