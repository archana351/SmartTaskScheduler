import java.util.List;

/**
 * TestScheduler.java
 * 
 * Simple test suite for verifying custom Data Structures and TaskManager logic.
 * Runs directly in Java without external testing frameworks like JUnit.
 */
public class TestScheduler {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  RUNNING SMART TASK SCHEDULER DSA & LOGIC TESTS  ");
        System.out.println("==================================================");

        testPriorityQueue();
        testQueue();
        testUndoStack();
        testLinkedList();
        testSorting();
        testTaskManager();

        System.out.println("==================================================");
        System.out.println("TEST SUMMARY:");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        if (testsFailed == 0) {
            System.out.println("STATUS: ALL TESTS PASSED SUCCESSFULLY! :)");
        } else {
            System.out.println("STATUS: SOME TESTS FAILED!");
        }
        System.out.println("==================================================");
    }

    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }

    private static void assertEquals(Object expected, Object actual, String testName) {
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected: " + expected + ", Got: " + actual);
            testsFailed++;
        }
    }

    // 1. Test Custom Priority Queue
    private static void testPriorityQueue() {
        System.out.println("\n--- Testing TaskPriorityQueue (Binary Max-Heap) ---");
        TaskPriorityQueue pq = new TaskPriorityQueue();

        assertTrue(pq.isEmpty(), "PQ starts empty");
        assertEquals(0, pq.size(), "PQ size is 0 initially");

        Task tLow = new Task(1, "Low Task", "desc", "LOW", "20-09-2026");
        Task tMed = new Task(2, "Med Task", "desc", "MEDIUM", "18-09-2026");
        Task tHighLate = new Task(3, "High Late", "desc", "HIGH", "25-09-2026");
        Task tHighEarly = new Task(4, "High Early", "desc", "HIGH", "10-09-2026");

        pq.add(tLow);
        pq.add(tMed);
        pq.add(tHighLate);
        pq.add(tHighEarly);

        assertEquals(4, pq.size(), "PQ size is 4 after additions");
        assertEquals(tHighEarly.getId(), pq.peek().getId(), "Peek returns High priority with earlier deadline");

        Task removed1 = pq.remove();
        assertEquals(tHighEarly.getId(), removed1.getId(), "First remove is High Early");

        Task removed2 = pq.remove();
        assertEquals(tHighLate.getId(), removed2.getId(), "Second remove is High Late");

        Task removed3 = pq.remove();
        assertEquals(tMed.getId(), removed3.getId(), "Third remove is Medium");

        Task removed4 = pq.remove();
        assertEquals(tLow.getId(), removed4.getId(), "Fourth remove is Low");

        assertTrue(pq.isEmpty(), "PQ is empty after removing all");
    }

    // 2. Test Custom FIFO Queue
    private static void testQueue() {
        System.out.println("\n--- Testing TaskQueue (FIFO Queue) ---");
        TaskQueue queue = new TaskQueue();

        assertTrue(queue.isEmpty(), "Queue starts empty");

        Task t1 = new Task(1, "First", "desc", "LOW", "10-09-2026");
        Task t2 = new Task(2, "Second", "desc", "MEDIUM", "12-09-2026");
        Task t3 = new Task(3, "Third", "desc", "HIGH", "15-09-2026");

        queue.enqueue(t1);
        queue.enqueue(t2);
        queue.enqueue(t3);

        assertEquals(3, queue.size(), "Queue size is 3");
        assertEquals("First", queue.peek().getTitle(), "Peek returns first enqueued task");

        assertEquals("First", queue.dequeue().getTitle(), "First dequeue returns First");
        assertEquals("Second", queue.dequeue().getTitle(), "Second dequeue returns Second");
        assertEquals("Third", queue.dequeue().getTitle(), "Third dequeue returns Third");
        assertTrue(queue.isEmpty(), "Queue is empty after 3 dequeues");
    }

    // 3. Test Custom Undo Stack
    private static void testUndoStack() {
        System.out.println("\n--- Testing UndoStack (LIFO Stack) ---");
        UndoStack stack = new UndoStack();

        assertTrue(stack.isEmpty(), "Stack starts empty");

        Task t1 = new Task(1, "Action 1", "desc", "LOW", "10-09-2026");
        Task t2 = new Task(2, "Action 2", "desc", "MEDIUM", "12-09-2026");

        stack.push(new UndoStack.Action(UndoStack.Action.ActionType.ADD, t1));
        stack.push(new UndoStack.Action(UndoStack.Action.ActionType.COMPLETE, t2));

        assertEquals(2, stack.size(), "Stack size is 2");
        assertEquals(UndoStack.Action.ActionType.COMPLETE, stack.peek().getType(), "Peek returns most recent action");

        UndoStack.Action popped1 = stack.pop();
        assertEquals(UndoStack.Action.ActionType.COMPLETE, popped1.getType(), "First pop returns COMPLETE (LIFO)");

        UndoStack.Action popped2 = stack.pop();
        assertEquals(UndoStack.Action.ActionType.ADD, popped2.getType(), "Second pop returns ADD (LIFO)");

        assertTrue(stack.isEmpty(), "Stack is empty after pops");
    }

    // 4. Test Custom Singly Linked List
    private static void testLinkedList() {
        System.out.println("\n--- Testing TaskLinkedList (Singly Linked List) ---");
        TaskLinkedList list = new TaskLinkedList();

        assertTrue(list.isEmpty(), "Linked List starts empty");

        Task t1 = new Task(1, "Task One", "desc", "LOW", "10-09-2026");
        Task t2 = new Task(2, "Task Two", "desc", "MEDIUM", "12-09-2026");
        Task t3 = new Task(3, "Task Three", "desc", "HIGH", "15-09-2026");

        list.add(t1);
        list.add(t2);
        list.add(t3);

        assertEquals(3, list.size(), "Linked list size is 3");
        assertTrue(list.contains(2), "Linked list contains Task ID 2");

        boolean removed = list.remove(2);
        assertTrue(removed, "Successfully removed Task ID 2");
        assertEquals(2, list.size(), "Linked list size is now 2");
        assertTrue(!list.contains(2), "Task ID 2 is no longer in list");
    }

    // 5. Test Bubble Sort
    private static void testSorting() {
        System.out.println("\n--- Testing Sorting (Bubble Sort) ---");
        TaskManager manager = new TaskManager();

        manager.addTask("Zebra Study", "desc", "LOW", "20-09-2026");
        manager.addTask("Alpha Coding", "desc", "HIGH", "15-09-2026");
        manager.addTask("Beta Testing", "desc", "MEDIUM", "10-09-2026");

        // Sort by Title (Alphabetical)
        manager.sortTasks("Title");
        List<Task> sortedByTitle = manager.getAllTasks();
        assertEquals("Alpha Coding", sortedByTitle.get(0).getTitle(), "Title sort 1st: Alpha Coding");
        assertEquals("Beta Testing", sortedByTitle.get(1).getTitle(), "Title sort 2nd: Beta Testing");
        assertEquals("Zebra Study", sortedByTitle.get(2).getTitle(), "Title sort 3rd: Zebra Study");

        // Sort by Priority (HIGH > MEDIUM > LOW)
        manager.sortTasks("Priority");
        List<Task> sortedByPriority = manager.getAllTasks();
        assertEquals("HIGH", sortedByPriority.get(0).getPriority(), "Priority sort 1st: HIGH");
        assertEquals("MEDIUM", sortedByPriority.get(1).getPriority(), "Priority sort 2nd: MEDIUM");
        assertEquals("LOW", sortedByPriority.get(2).getPriority(), "Priority sort 3rd: LOW");

        // Sort by Deadline (earliest first)
        manager.sortTasks("Deadline");
        List<Task> sortedByDeadline = manager.getAllTasks();
        assertEquals("10-09-2026", sortedByDeadline.get(0).getDeadline(), "Deadline sort 1st: 10-09-2026");
        assertEquals("15-09-2026", sortedByDeadline.get(1).getDeadline(), "Deadline sort 2nd: 15-09-2026");
        assertEquals("20-09-2026", sortedByDeadline.get(2).getDeadline(), "Deadline sort 3rd: 20-09-2026");
    }

    // 6. Test TaskManager (Add, Delete, Complete, Search, Undo, Next Task)
    private static void testTaskManager() {
        System.out.println("\n--- Testing TaskManager Coordination ---");
        TaskManager manager = new TaskManager();

        // 1. Add task
        Task t1 = manager.addTask("Study Java", "OOP notes", "HIGH", "15-09-2026");
        Task t2 = manager.addTask("Practice SQL", "Joins query", "MEDIUM", "16-09-2026");
        assertEquals(2, manager.getAllTasks().size(), "TaskManager added 2 tasks");

        // 2. HashMap fast lookup by ID
        Task lookup = manager.getTaskById(t1.getId());
        assertEquals(t1.getTitle(), lookup.getTitle(), "HashMap finds task by ID in O(1)");

        // 3. Search
        List<Task> searchById = manager.searchTasks(String.valueOf(t2.getId()));
        assertEquals(1, searchById.size(), "Search by numeric ID returns 1 match");
        assertEquals(t2.getTitle(), searchById.get(0).getTitle(), "Search by ID found correct task");

        List<Task> searchByTitle = manager.searchTasks("sql");
        assertEquals(1, searchByTitle.size(), "Search by title substring 'sql' found 1 match");

        // 4. Complete task
        boolean completed = manager.completeTask(t1.getId());
        assertTrue(completed, "Marked task as completed");
        assertTrue(t1.isCompleted(), "Task status is now completed");

        // 5. Next Task recommendation (t1 is completed, so t2 should be next)
        Task next = manager.getNextTask();
        assertEquals(t2.getId(), next.getId(), "Next task recommends incomplete task (t2)");

        // 6. Undo Complete
        manager.undo();
        assertTrue(!t1.isCompleted(), "Undo restored t1 back to pending status");

        // 7. Delete task and Undo
        int deleteId = t2.getId();
        manager.deleteTask(deleteId);
        assertEquals(1, manager.getAllTasks().size(), "Task deleted; 1 remaining");

        manager.undo();
        assertEquals(2, manager.getAllTasks().size(), "Undo restored deleted task; 2 total again");
    }
}
