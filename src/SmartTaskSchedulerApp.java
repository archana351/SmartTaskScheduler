import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * SmartTaskSchedulerApp.java
 * 
 * Modern JavaFX Desktop Application for Smart Task Scheduler.
 * 
 * Replaces the Swing UI with a clean, modern dark productivity-dashboard:
 * - Built with standard JavaFX components: Stage, Scene, VBox, HBox, BorderPane, GridPane,
 *   Label, Button, TextField, TextArea, ComboBox, TableView, Alert.
 * - Powered by custom DSA logic in TaskManager (Priority Queue, Queue, Stack, Linked List, HashMap, Bubble Sort).
 * - Styled with JavaFX CSS.
 */
public class SmartTaskSchedulerApp extends Application {

    private TaskManager taskManager;

    // Metric summary labels (Dashboard cards)
    private Label lblTotalTasks;
    private Label lblPendingTasks;
    private Label lblCompletedTasks;
    private Label lblHighPriorityTasks;

    // Form inputs
    private TextField titleField;
    private TextField descField;
    private ComboBox<String> priorityComboBox;
    private TextField deadlineField;
    private Button addButton;

    // Search and Sort controls
    private TextField searchField;
    private Button searchButton;
    private Button showAllButton;
    private ComboBox<String> sortComboBox;
    private Button sortButton;

    // TableView
    private TableView<Task> taskTable;

    // Action buttons
    private Button completeButton;
    private Button deleteButton;
    private Button undoButton;
    private Button nextTaskButton;
    private Button viewDsaButton;

    // Status label at footer
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Initialize coordinator and sample tasks
        this.taskManager = new TaskManager();
        this.taskManager.loadSampleTasks();

        primaryStage.setTitle("Smart Task Scheduler");

        // Main layout container: BorderPane with dark background
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // 1. Top Section: Header + Metrics Summary Cards
        VBox topContainer = new VBox(12);
        topContainer.setPadding(new Insets(16, 20, 10, 20));

        VBox headerBox = createHeader();
        HBox statsBox = createStatsCards();
        topContainer.getChildren().addAll(headerBox, statsBox);
        root.setTop(topContainer);

        // 2. Center Section: Add Task Card + Search/Sort + TableView
        VBox centerContainer = new VBox(12);
        centerContainer.setPadding(new Insets(6, 20, 12, 20));

        VBox formCard = createFormCard();
        HBox searchSortBar = createSearchSortBar();
        VBox tableCard = createTableCard();

        centerContainer.getChildren().addAll(formCard, searchSortBar, tableCard);
        root.setCenter(centerContainer);

        // 3. Bottom Section: Action Buttons + Footer (Status & Secondary DSA Button)
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(6, 20, 14, 20));

        HBox actionButtonsBar = createActionButtons();

        // Footer row: status on left, small subtle DSA Status button on bottom-right
        HBox footerBar = new HBox(10);
        footerBar.setAlignment(Pos.CENTER_LEFT);

        statusLabel = new Label("Ready. 7 sample tasks loaded.");
        statusLabel.getStyleClass().add("status-bar");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        viewDsaButton = new Button("⚙ DSA Status");
        viewDsaButton.getStyleClass().add("btn-dsa-subtle");
        viewDsaButton.setTooltip(new Tooltip("Inspect live Data Structures (Priority Queue, Queue, Stack, Linked List, HashMap)"));

        footerBar.getChildren().addAll(statusLabel, footerSpacer, viewDsaButton);

        bottomContainer.getChildren().addAll(actionButtonsBar, footerBar);
        root.setBottom(bottomContainer);

        // Setup event handlers
        setupEventHandlers(primaryStage);

        // Populate table and statistics
        refreshTable();

        // Create Scene and attach CSS
        Scene scene = new Scene(root, 980, 750);
        attachStylesheet(scene);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(860);
        primaryStage.setMinHeight(640);
        primaryStage.show();
    }

    /**
     * Header with application title and subtitle.
     */
    private VBox createHeader() {
        VBox box = new VBox(2);
        Label title = new Label("SMART TASK SCHEDULER");
        title.getStyleClass().add("app-title");

        Label subtitle = new Label("Organize your tasks, prioritize your work, and stay productive");
        subtitle.getStyleClass().add("app-subtitle");

        box.getChildren().addAll(title, subtitle);
        return box;
    }

    /**
     * Top Dashboard: Rounded metric cards for Total, Pending, Completed, High Priority.
     */
    private HBox createStatsCards() {
        HBox statsRow = new HBox(14);
        statsRow.setAlignment(Pos.CENTER);

        // 1. Total Tasks Card
        VBox cardTotal = createMetricCard("TOTAL TASKS", "stat-value-blue");
        lblTotalTasks = (Label) cardTotal.getChildren().get(1);

        // 2. Pending Tasks Card
        VBox cardPending = createMetricCard("PENDING", "stat-value-amber");
        lblPendingTasks = (Label) cardPending.getChildren().get(1);

        // 3. Completed Tasks Card
        VBox cardCompleted = createMetricCard("COMPLETED", "stat-value-green");
        lblCompletedTasks = (Label) cardCompleted.getChildren().get(1);

        // 4. High Priority Card
        VBox cardHigh = createMetricCard("HIGH PRIORITY", "stat-value-red");
        lblHighPriorityTasks = (Label) cardHigh.getChildren().get(1);

        HBox.setHgrow(cardTotal, Priority.ALWAYS);
        HBox.setHgrow(cardPending, Priority.ALWAYS);
        HBox.setHgrow(cardCompleted, Priority.ALWAYS);
        HBox.setHgrow(cardHigh, Priority.ALWAYS);

        statsRow.getChildren().addAll(cardTotal, cardPending, cardCompleted, cardHigh);
        return statsRow;
    }

    private VBox createMetricCard(String labelText, String colorStyleClass) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");

        Label label = new Label(labelText);
        label.getStyleClass().add("stat-label");

        Label value = new Label("0");
        value.getStyleClass().addAll("stat-value", colorStyleClass);

        card.getChildren().addAll(label, value);
        return card;
    }

    /**
     * "Add New Task" Card with input fields.
     */
    private VBox createFormCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");

        Label cardTitle = new Label("Add New Task");
        cardTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(8);

        // Row 0: Title & Description
        Label lblTitle = new Label("Title:");
        lblTitle.getStyleClass().add("field-label");
        titleField = new TextField();
        titleField.setPromptText("e.g. Study Java");
        titleField.setPrefWidth(220);

        Label lblDesc = new Label("Description:");
        lblDesc.getStyleClass().add("field-label");
        descField = new TextField();
        descField.setPromptText("e.g. Review OOP concepts");
        descField.setPrefWidth(300);

        grid.add(lblTitle, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(lblDesc, 2, 0);
        grid.add(descField, 3, 0);

        // Row 1: Priority, Deadline & Add Button
        Label lblPriority = new Label("Priority:");
        lblPriority.getStyleClass().add("field-label");
        priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("HIGH", "MEDIUM", "LOW");
        priorityComboBox.setValue("HIGH");
        priorityComboBox.setPrefWidth(120);

        Label lblDeadline = new Label("Deadline:");
        lblDeadline.getStyleClass().add("field-label");
        deadlineField = new TextField("16-09-2026");
        deadlineField.setPromptText("DD-MM-YYYY");
        deadlineField.setPrefWidth(130);

        addButton = new Button("+ Add Task");
        addButton.getStyleClass().addAll("btn", "btn-primary");

        grid.add(lblPriority, 0, 1);
        grid.add(priorityComboBox, 1, 1);
        grid.add(lblDeadline, 2, 1);

        HBox deadlineAddBox = new HBox(10, deadlineField, addButton);
        deadlineAddBox.setAlignment(Pos.CENTER_LEFT);
        grid.add(deadlineAddBox, 3, 1);

        GridPane.setHgrow(descField, Priority.ALWAYS);
        card.getChildren().addAll(cardTitle, grid);
        return card;
    }

    /**
     * Search and Sort Bar.
     */
    private HBox createSearchSortBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);

        // Search components on left
        Label searchLabel = new Label("Search:");
        searchLabel.getStyleClass().add("field-label");
        searchField = new TextField();
        searchField.setPromptText("Search by ID or Title...");
        searchField.setPrefWidth(200);

        searchButton = new Button("Search");
        searchButton.getStyleClass().addAll("btn", "btn-primary");

        showAllButton = new Button("Show All");
        showAllButton.getStyleClass().addAll("btn", "btn-secondary");

        HBox searchGroup = new HBox(6, searchLabel, searchField, searchButton, showAllButton);
        searchGroup.setAlignment(Pos.CENTER_LEFT);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Sort components on right
        Label sortLabel = new Label("Sort By:");
        sortLabel.getStyleClass().add("field-label");
        sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Priority", "Deadline", "Title");
        sortComboBox.setValue("Priority");

        sortButton = new Button("Sort");
        sortButton.getStyleClass().addAll("btn", "btn-cyan");

        HBox sortGroup = new HBox(6, sortLabel, sortComboBox, sortButton);
        sortGroup.setAlignment(Pos.CENTER_RIGHT);

        bar.getChildren().addAll(searchGroup, spacer, sortGroup);
        return bar;
    }

    /**
     * TableView inside card container.
     */
    @SuppressWarnings("unchecked")
    private VBox createTableCard() {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        VBox.setVgrow(card, Priority.ALWAYS);

        Label tableTitle = new Label("Tasks");
        tableTitle.getStyleClass().add("card-title");

        taskTable = new TableView<>();
        taskTable.setPlaceholder(new Label("No tasks found."));
        VBox.setVgrow(taskTable, Priority.ALWAYS);

        // Columns
        TableColumn<Task, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Task, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Task, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(240);

        TableColumn<Task, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(95);
        priorityCol.setStyle("-fx-alignment: CENTER;");
        priorityCol.setCellFactory(col -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(priority);
                    badge.getStyleClass().add("badge");
                    if ("HIGH".equalsIgnoreCase(priority)) {
                        badge.getStyleClass().add("badge-high");
                    } else if ("MEDIUM".equalsIgnoreCase(priority)) {
                        badge.getStyleClass().add("badge-medium");
                    } else {
                        badge.getStyleClass().add("badge-low");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Task, String> deadlineCol = new TableColumn<>("Deadline");
        deadlineCol.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        deadlineCol.setPrefWidth(100);
        deadlineCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Task, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(105);
        statusCol.setStyle("-fx-alignment: CENTER;");
        statusCol.setCellFactory(col -> new TableCell<Task, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    if ("Completed".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-completed");
                    } else {
                        badge.getStyleClass().add("badge-pending");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        taskTable.getColumns().addAll(idCol, titleCol, descCol, priorityCol, deadlineCol, statusCol);
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        card.getChildren().addAll(tableTitle, taskTable);
        return card;
    }

    /**
     * Bottom Action Buttons.
     */
    private HBox createActionButtons() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER);

        completeButton = new Button("✓ Complete Task");
        completeButton.getStyleClass().addAll("btn", "btn-success");

        deleteButton = new Button("✕ Delete Task");
        deleteButton.getStyleClass().addAll("btn", "btn-danger");

        undoButton = new Button("⟲ Undo");
        undoButton.getStyleClass().addAll("btn", "btn-secondary");

        nextTaskButton = new Button("★ Next Task (Priority)");
        nextTaskButton.getStyleClass().addAll("btn", "btn-purple");

        bar.getChildren().addAll(completeButton, deleteButton, undoButton, nextTaskButton);
        return bar;
    }

    /**
     * Configures event handlers for all buttons and interactive controls.
     */
    private void setupEventHandlers(Stage stage) {
        // 1. Add Task
        addButton.setOnAction(e -> handleAddTask());

        // 2. Complete Task
        completeButton.setOnAction(e -> handleCompleteTask());

        // 3. Delete Task (with confirmation Alert)
        deleteButton.setOnAction(e -> handleDeleteTask());

        // 4. Undo
        undoButton.setOnAction(e -> handleUndo());

        // 5. Next Task recommendation
        nextTaskButton.setOnAction(e -> handleNextTask());

        // 6. Search
        searchButton.setOnAction(e -> handleSearch());
        searchField.setOnAction(e -> handleSearch()); // Enter key triggers search

        // 7. Show All
        showAllButton.setOnAction(e -> {
            searchField.clear();
            refreshTable();
            statusLabel.setText("Showing all " + taskManager.getAllTasks().size() + " tasks.");
        });

        // 8. Sort (Bubble Sort)
        sortButton.setOnAction(e -> handleSort());

        // 9. View DSA Status
        viewDsaButton.setOnAction(e -> handleViewDsa());
    }

    /**
     * Refreshes the TableView and metric cards with current task list.
     */
    private void refreshTable() {
        List<Task> tasks = taskManager.getAllTasks();
        taskTable.getItems().setAll(tasks);
        updateStatistics();
    }

    /**
     * Updates top statistics cards.
     */
    private void updateStatistics() {
        List<Task> all = taskManager.getAllTasks();
        int total = all.size();
        int completed = 0;
        int high = 0;

        for (Task t : all) {
            if (t.isCompleted()) {
                completed++;
            } else {
                if ("HIGH".equalsIgnoreCase(t.getPriority())) {
                    high++;
                }
            }
        }
        int pending = total - completed;

        lblTotalTasks.setText(String.valueOf(total));
        lblPendingTasks.setText(String.valueOf(pending));
        lblCompletedTasks.setText(String.valueOf(completed));
        lblHighPriorityTasks.setText(String.valueOf(high));
    }

    /**
     * Handles adding a new task.
     */
    private void handleAddTask() {
        String title = titleField.getText().trim();
        String desc = descField.getText().trim();
        String priority = priorityComboBox.getValue();
        String deadline = deadlineField.getText().trim();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a task title.");
            titleField.requestFocus();
            return;
        }

        if (deadline.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please enter a deadline (e.g. DD-MM-YYYY).");
            deadlineField.requestFocus();
            return;
        }

        Task newTask = taskManager.addTask(title, desc, priority, deadline);
        refreshTable();

        titleField.clear();
        descField.clear();

        statusLabel.setText("Task added: \"" + newTask.getTitle() + "\" (ID: " + newTask.getId() + ")");
    }

    /**
     * Handles completing the selected task.
     */
    private void handleCompleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a task from the table to complete.");
            return;
        }

        if (selected.isCompleted()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Completed", "Task \"" + selected.getTitle() + "\" is already marked as completed.");
            return;
        }

        boolean success = taskManager.completeTask(selected.getId());
        if (success) {
            refreshTable();
            statusLabel.setText("Marked Task #" + selected.getId() + " (" + selected.getTitle() + ") as Completed.");
        }
    }

    /**
     * Handles deleting the selected task with an Alert confirmation.
     */
    private void handleDeleteTask() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select a task from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Task: \"" + selected.getTitle() + "\" (ID: " + selected.getId() + ")?");
        confirm.setContentText("Are you sure you want to delete this task?\nYou can click 'Undo' to restore it.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            int id = selected.getId();
            taskManager.deleteTask(id);
            refreshTable();
            statusLabel.setText("Deleted task #" + id + ". Click 'Undo' to restore it.");
        }
    }

    /**
     * Handles undoing the last user action.
     */
    private void handleUndo() {
        String undoResult = taskManager.undo();
        refreshTable();
        statusLabel.setText(undoResult);
        showAlert(Alert.AlertType.INFORMATION, "Undo Action", undoResult);
    }

    /**
     * Recommends next task using Priority Queue.
     */
    private void handleNextTask() {
        Task next = taskManager.getNextTask();
        if (next == null) {
            showAlert(Alert.AlertType.INFORMATION, "Next Task Recommendation", "No pending tasks found!\nAll tasks are completed or list is empty.");
            return;
        }

        String msg = "Recommended Task:\n"
                + "----------------------------------------\n"
                + "Title:       " + next.getTitle() + "\n"
                + "ID:          " + next.getId() + "\n"
                + "Priority:    " + next.getPriority() + "\n"
                + "Deadline:    " + next.getDeadline() + "\n"
                + "Description: " + (next.getDescription().isEmpty() ? "(None)" : next.getDescription()) + "\n\n"
                + "Reason:\n"
                + "High priority and earliest deadline.\n"
                + "(Determined by TaskPriorityQueue Binary Max-Heap)";

        showAlert(Alert.AlertType.INFORMATION, "Smart Recommended Task", msg);

        // Highlight and scroll to task in TableView
        taskTable.getSelectionModel().select(next);
        taskTable.scrollTo(next);
    }

    /**
     * Handles search by task ID (HashMap O(1)) or Title substring.
     */
    private void handleSearch() {
        String query = searchField.getText().trim();
        List<Task> results = taskManager.searchTasks(query);
        taskTable.getItems().setAll(results);
        updateStatistics();

        if (results.isEmpty()) {
            statusLabel.setText("No tasks matching \"" + query + "\" were found.");
        } else {
            statusLabel.setText("Found " + results.size() + " matching task(s).");
        }
    }

    /**
     * Handles Bubble Sort by selected criterion.
     */
    private void handleSort() {
        String criteria = sortComboBox.getValue();
        taskManager.sortTasks(criteria);
        refreshTable();
        statusLabel.setText("Tasks sorted by " + criteria + ".");
    }

    /**
     * Shows a modal dialog with formatted DSA status overview.
     */
    private void handleViewDsa() {
        String dsaInfo = taskManager.getDsaStatus();

        TextArea textArea = new TextArea(dsaInfo);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0f121a; -fx-text-fill: #38bdf8;");
        textArea.setPrefSize(580, 420);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Structures Status");
        alert.setHeaderText("DSA Status Inspection");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    /**
     * Helper to show a simple Alert dialog.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Helper to load and attach external CSS file.
     */
    private void attachStylesheet(Scene scene) {
        try {
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            } else if (new File("src/style.css").exists()) {
                scene.getStylesheets().add(new File("src/style.css").toURI().toString());
            }
        } catch (Exception ignored) {
            // Safe fallback if css path resolves differently
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
