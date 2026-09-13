# Smart Task Scheduler (JavaFX Edition)

A modern, desktop task management application built using **Core Java**, **JavaFX**, and **Custom Data Structures & Algorithms (DSA)**.

---

## 📌 Project Overview

**Smart Task Scheduler** demonstrates how fundamental Data Structures and Algorithms (Priority Queue, Queue, Stack, Linked List, HashMap, and Bubble Sort) integrate into a sleek, modern desktop productivity dashboard styled in dark blue/charcoal with reactive metrics cards and interactive controls.

---

## 🛠️ Technologies Used

* **Core Java (Java 11+)**
* **JavaFX (Controls & Graphics)** (Modern desktop GUI: `Stage`, `Scene`, `VBox`, `HBox`, `BorderPane`, `GridPane`, `TableView`, `Alert`, etc.)
* **JavaFX CSS** (Modern dark-theme dashboard stylesheet: `#12151d` charcoal palette, rounded cards, custom priority & status badges)
* **Custom Data Structures & Algorithms (DSA)** (Implemented from scratch, zero external frameworks)

---

## 🧠 Data Structures & Algorithms Implemented

| Data Structure / Algorithm | File | Role & Implementation |
| :--- | :--- | :--- |
| **Priority Queue** | [`src/TaskPriorityQueue.java`](src/TaskPriorityQueue.java) | **Binary Max-Heap** using an array. Schedules the most urgent task based on priority rank (`HIGH` > `MEDIUM` > `LOW`) and earlier deadline for ties. |
| **FIFO Queue** | [`src/TaskQueue.java`](src/TaskQueue.java) | **First-In First-Out Queue** implemented using linked nodes. Tracks pending tasks in arrival order (`enqueue`, `dequeue`, `peek`). |
| **Undo Stack** | [`src/UndoStack.java`](src/UndoStack.java) | **LIFO Stack** using dynamic array. Records user operations (`ADD`, `DELETE`, `COMPLETE`) to reverse recent actions. |
| **Linked List** | [`src/TaskLinkedList.java`](src/TaskLinkedList.java) | **Singly Linked List** (`head`, `next` pointers). Maintains completed task history. |
| **HashMap** | Built-in `java.util.HashMap` in [`src/TaskManager.java`](src/TaskManager.java) | Maps `taskId (Integer)` $\rightarrow$ `Task` for instant **$O(1)$ fast lookup** by task ID. |
| **Bubble Sort** | [`src/TaskManager.java`](src/TaskManager.java) | In-place manual sorting algorithm. Sorts tasks by **Priority**, **Deadline**, or **Title**. |

---

## 📊 Modern Dashboard Layout

<img width="1910" height="1012" alt="image" src="https://github.com/user-attachments/assets/930aa002-1472-4204-aacf-e5d7abd517ad" />


## ✨ Features

1. **Reactive Metrics Cards**: Real-time counter cards at the top displaying **Total Tasks**, **Pending Tasks**, **Completed Tasks**, and **High Priority Tasks**.
2. **Add Task**: Quickly schedules tasks with Title, Description, Priority (`HIGH`, `MEDIUM`, `LOW`), and Deadline.
3. **TableView Display**: Interactive JavaFX `TableView` with color-coded priority badges and completion status tags.
4. **Smart Next-Task Recommendation**: Queries the custom **Binary Max-Heap** to recommend the next task to tackle, detailing the reasoning in a dialog and selecting it in the table.
5. **Mark Completed**: Marks selected tasks completed and moves them to the completed history Linked List.
6. **Delete Task**: Removes tasks with a JavaFX confirmation Alert.
7. **Undo**: Reverses the last `Add`, `Delete`, or `Complete` operation using the `UndoStack`.
8. **Instant Search**: $O(1)$ fast ID lookup via `HashMap` or substring search across task titles.
9. **Manual Sorting**: Sorts tasks using Bubble Sort by Priority, Deadline, or Title.
10. **View DSA Status**: Displays live, formatted status of all 5 data structures in a monospace inspection dialog.

---

## 📁 Project Structure

```text
SmartTaskScheduler/
│
├── lib/                          # Local JavaFX libraries (base, controls, graphics)
│   ├── javafx-base-21.0.2-win.jar
│   ├── javafx-controls-21.0.2-win.jar
│   └── javafx-graphics-21.0.2-win.jar
│
├── src/
│   ├── Main.java                 # Entry point launcher
│   ├── SmartTaskSchedulerApp.java# JavaFX Application (BorderPane, TableView, Cards, Alerts)
│   ├── style.css                 # Modern dark-theme stylesheet
│   ├── Task.java                 # Task model with getters, setters, and date comparison
│   ├── TaskManager.java          # Core logic, HashMap registry, and Bubble Sort
│   ├── TaskPriorityQueue.java    # Custom Binary Max-Heap Priority Queue
│   ├── TaskQueue.java            # Custom FIFO Queue with linked nodes
│   ├── UndoStack.java            # Custom LIFO Stack for undo operations
│   ├── TaskLinkedList.java       # Custom Singly Linked List for completed history
│   └── TestScheduler.java        # Automated verification tests (48 tests, zero external dependencies)
│
├── .vscode/                      # Pre-configured launch & library settings
│   ├── launch.json
│   └── settings.json
│
├── pom.xml                       # Optional Maven configuration for dependency management
├── run.bat                       # One-click Windows runner script
├── .gitignore                    # Ignores build artifacts and cache
└── README.md                     # Documentation
```

---

## 🚀 How to Run the Application

### Method 1: Using the Terminal / PowerShell (Recommended)

1. **Compile the JavaFX application**:
   ```powershell
   javac --module-path lib --add-modules javafx.controls -d out src/*.java
   Copy-Item "src\style.css" -Destination "out\style.css"
   ```

2. **Launch the application**:
   ```powershell
   java --module-path lib --add-modules javafx.controls -cp out Main
   ```

3. **Or simply run the batch script (Windows)**:
   ```cmd
   .\run.bat
   ```

---

### Method 2: In VS Code or Cursor (1-Click Run)

1. Open the project folder in **VS Code** or **Cursor**.
2. Open [`src/Main.java`](src/Main.java).
3. Press **F5** or click the **▷ Run** button above `main`.
4. The JavaFX desktop window will launch immediately with full styling.

---

### Method 3: Running the Automated DSA Tests

To verify that all custom Data Structures and application logic function correctly:

```powershell
java -cp out TestScheduler
```
*(All 48 tests will execute in the console with zero dependencies!)*
