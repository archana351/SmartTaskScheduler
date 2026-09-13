import java.util.ArrayList;
import java.util.List;

/**
 * UndoStack.java
 * 
 * Custom Data Structure: LIFO Stack (Last-In, First-Out).
 * 
 * Records user actions (ADD, DELETE, COMPLETE) so they can be
 * undone in reverse chronological order.
 */
public class UndoStack {

    /**
     * Represents an operation recorded for undoing.
     */
    public static class Action {
        public enum ActionType { ADD, DELETE, COMPLETE }

        private final ActionType type;
        private final Task task;

        public Action(ActionType type, Task task) {
            this.type = type;
            this.task = task;
        }

        public ActionType getType() {
            return type;
        }

        public Task getTask() {
            return task;
        }

        @Override
        public String toString() {
            switch (type) {
                case ADD:
                    return "Add Task (" + task.getTitle() + ")";
                case DELETE:
                    return "Delete Task (" + task.getTitle() + ")";
                case COMPLETE:
                    return "Complete Task (" + task.getTitle() + ")";
                default:
                    return type + " Task (" + task.getTitle() + ")";
            }
        }
    }

    private Action[] elements;
    private int top; // Index of top element (-1 when empty)
    private static final int INITIAL_CAPACITY = 20;

    public UndoStack() {
        this.elements = new Action[INITIAL_CAPACITY];
        this.top = -1;
    }

    /**
     * Pushes an action onto the top of the stack.
     */
    public void push(Action action) {
        if (action == null) return;

        if (top == elements.length - 1) {
            resize();
        }
        elements[++top] = action;
    }

    /**
     * Removes and returns the action at the top of the stack.
     */
    public Action pop() {
        if (isEmpty()) {
            return null;
        }
        Action action = elements[top];
        elements[top] = null; // Help garbage collection
        top--;
        return action;
    }

    /**
     * Returns the action at the top of the stack without removing it.
     */
    public Action peek() {
        if (isEmpty()) {
            return null;
        }
        return elements[top];
    }

    /**
     * Checks if the stack is empty.
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Returns the number of actions in the stack.
     */
    public int size() {
        return top + 1;
    }

    /**
     * Clears all actions in the stack.
     */
    public void clear() {
        for (int i = 0; i <= top; i++) {
            elements[i] = null;
        }
        top = -1;
    }

    /**
     * Doubles the capacity of the internal array.
     */
    private void resize() {
        Action[] newArray = new Action[elements.length * 2];
        System.arraycopy(elements, 0, newArray, 0, elements.length);
        elements = newArray;
    }

    /**
     * Returns a list of actions from top to bottom (most recent first).
     */
    public List<Action> getAllActions() {
        List<Action> list = new ArrayList<>();
        for (int i = top; i >= 0; i--) {
            list.add(elements[i]);
        }
        return list;
    }

    /**
     * Returns a formatted string representation of the stack.
     */
    public String display() {
        if (isEmpty()) return "(Empty)";

        StringBuilder sb = new StringBuilder();
        for (int i = top; i >= 0; i--) {
            sb.append(elements[i].toString());
            if (i > 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
