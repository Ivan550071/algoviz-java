package ui;

import algorithms.trees.BstStepper;
import controller.PlaybackController;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.steps.AlgoStep;
import model.steps.HighlightNodeStep;
import model.tree.BstTree;
import visualization.TreeRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeView extends VBox {
    private final Canvas canvas = new Canvas(900, 520);

    private final TextField valueField = new TextField();
    private final Slider speedSlider = new Slider(0.1, 3.0, 1.0);
    private final ComboBox<String> traversalBox = new ComboBox<>();

    private final Label status = new Label("Ready");
    private final Label opLabel = new Label("Current op: None");
    private final Label stepsLabel = new Label("Steps played: 0");
    private final Label inOrderLabel = new Label("In-order: []");
    private final Label searchResultLabel = new Label("Search result: —");

    private final TextArea pseudocodeArea = new TextArea();

    private final BstTree tree = new BstTree();
    private final TreeRenderer renderer = new TreeRenderer();

    private final Set<Integer> highlightedValues = new HashSet<>();
    private List<AlgoStep> currentSteps = new ArrayList<>();
    private int stepsPlayed = 0;

    // Track current operation mode so pseudocode panel can switch
    private enum TreeMode { SEARCH, TRAVERSAL }
    private TreeMode currentMode = TreeMode.SEARCH;

    public TreeView(PlaybackController playbackController) {
        setSpacing(10);
        setPadding(new Insets(10));

        valueField.setPromptText("Enter integer");
        valueField.setPrefWidth(120);

        traversalBox.getItems().addAll("In-order", "Pre-order", "Post-order");
        traversalBox.getSelectionModel().select("In-order");
        traversalBox.setPrefWidth(130);

        pseudocodeArea.setEditable(false);
        pseudocodeArea.setWrapText(false);
        pseudocodeArea.setPrefRowCount(18);
        pseudocodeArea.setPrefColumnCount(34);
        pseudocodeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");
        pseudocodeArea.setText(getSearchPseudocode());

        Button insertBtn = new Button("Insert");
        Button searchBtn = new Button("Search");
        Button traverseBtn = new Button("Traverse");
        Button sampleBtn = new Button("Load Sample");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stepBtn = new Button("Step");
        Button resetAnimBtn = new Button("Reset Animation");
        Button clearTreeBtn = new Button("Clear Tree");

        insertBtn.setOnAction(e -> {
            Integer value = parseInputValue();
            if (value == null) return;

            boolean inserted = tree.insert(value);

            highlightedValues.clear();
            currentSteps = new ArrayList<>();
            playbackController.reset();

            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");

            opLabel.setText("Current op: Insert " + value + (inserted ? " (ok)" : " (duplicate ignored)"));
            inOrderLabel.setText("In-order: " + tree.inOrderValues());
            status.setText(inserted ? "Inserted " + value : "Duplicate ignored: " + value);

            // insert doesn't affect search result directly, leave it as-is
            render();
        });

        searchBtn.setOnAction(e -> {
            Integer value = parseInputValue();
            if (value == null) return;

            currentMode = TreeMode.SEARCH;
            pseudocodeArea.setText(getSearchPseudocode());

            highlightedValues.clear();
            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");

            currentSteps = BstStepper.generateSearchSteps(tree, value);

            boolean found = !currentSteps.isEmpty() && lastHighlightedValue(currentSteps) == value;
            searchResultLabel.setText(found ? "Search result: Found " + value : "Search result: Not found " + value);

            int[] dummyState = new int[0];
            playbackController.loadSteps(dummyState, currentSteps, (ignoredArray, lastStep) -> {
                applyTreeStep(lastStep);
                render();
            });

            status.setText("Generated " + currentSteps.size() + " search steps for " + value);
        });

        traverseBtn.setOnAction(e -> {
            highlightedValues.clear();
            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");

            currentMode = TreeMode.TRAVERSAL;
            String traversal = traversalBox.getValue();
            pseudocodeArea.setText(getTraversalPseudocode(traversal));

            if ("Pre-order".equals(traversal)) {
                currentSteps = BstStepper.generatePreOrderTraversalSteps(tree);
            } else if ("Post-order".equals(traversal)) {
                currentSteps = BstStepper.generatePostOrderTraversalSteps(tree);
            } else {
                currentSteps = BstStepper.generateInOrderTraversalSteps(tree);
            }

            int[] dummyState = new int[0];
            playbackController.loadSteps(dummyState, currentSteps, (ignoredArray, lastStep) -> {
                applyTreeStep(lastStep);
                render();
            });

            if ("In-order".equals(traversal)) {
                inOrderLabel.setText("In-order: " + tree.inOrderValues());
            } else {
                inOrderLabel.setText(traversal + ": (see animation)");
            }

            status.setText("Generated " + currentSteps.size() + " " + traversal + " traversal steps");
        });

        sampleBtn.setOnAction(e -> {
            tree.clear();
            int[] sample = {50, 30, 70, 20, 40, 60, 80, 35, 45};
            for (int v : sample) {
                tree.insert(v);
            }

            highlightedValues.clear();
            currentSteps = new ArrayList<>();
            playbackController.reset();

            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");
            opLabel.setText("Current op: Loaded sample tree");
            inOrderLabel.setText("In-order: " + tree.inOrderValues());
            searchResultLabel.setText("Search result: —");
            status.setText("Loaded sample BST");

            render();
        });

        playBtn.setOnAction(e -> playbackController.play(speedSlider.getValue()));
        pauseBtn.setOnAction(e -> playbackController.pause());
        stepBtn.setOnAction(e -> playbackController.stepForward());

        resetAnimBtn.setOnAction(e -> {
            playbackController.reset();
            highlightedValues.clear();
            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");
            opLabel.setText("Current op: None");
            status.setText("Reset animation");
            render();
        });

        clearTreeBtn.setOnAction(e -> {
            playbackController.reset();
            tree.clear();
            highlightedValues.clear();
            currentSteps = new ArrayList<>();
            stepsPlayed = 0;
            stepsLabel.setText("Steps played: 0");
            opLabel.setText("Current op: None");
            inOrderLabel.setText("In-order: []");
            searchResultLabel.setText("Search result: —");
            status.setText("Cleared tree");
            render();
        });

        speedSlider.valueProperty().addListener((obs, oldV, newV) ->
                playbackController.setSpeedMultiplier(newV.doubleValue()));

        traversalBox.valueProperty().addListener((obs, oldV, newV) -> {
            if (currentMode == TreeMode.TRAVERSAL) {
                pseudocodeArea.setText(getTraversalPseudocode(newV));
            }
        });

        HBox controls = new HBox(8,
                new Label("Value:"), valueField,
                insertBtn, searchBtn,
                new Label("Traversal:"), traversalBox, traverseBtn,
                sampleBtn,
                new Label("Speed:"), speedSlider,
                playBtn, pauseBtn, stepBtn, resetAnimBtn, clearTreeBtn
        );
        HBox.setHgrow(speedSlider, Priority.ALWAYS);

        HBox infoRow = new HBox(20, opLabel, stepsLabel);
        VBox leftInfoBox = new VBox(6, infoRow, inOrderLabel, searchResultLabel, status);

        Label pseudoTitle = new Label("Pseudocode");
        pseudoTitle.setStyle("-fx-font-weight: bold;");

        VBox rightPanel = new VBox(8, pseudoTitle, pseudocodeArea);
        rightPanel.setPadding(new Insets(8));
        rightPanel.setPrefWidth(360);
        rightPanel.setMinWidth(320);
        rightPanel.setStyle(
                "-fx-background-color: #f6f8fa;" +
                        "-fx-border-color: #d0d7de;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
        );
        VBox.setVgrow(pseudocodeArea, Priority.ALWAYS);

        VBox leftPanel = new VBox(8, leftInfoBox, canvas);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        HBox mainContent = new HBox(12, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        getChildren().addAll(controls, mainContent);

        render();
    }

    private Integer parseInputValue() {
        String text = valueField.getText();
        if (text == null || text.isBlank()) {
            status.setText("Enter an integer value first");
            return null;
        }

        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            status.setText("Invalid integer: " + text);
            return null;
        }
    }

    private void applyTreeStep(AlgoStep step) {
        if (step == null) return;

        stepsPlayed++;
        stepsLabel.setText("Steps played: " + stepsPlayed);

        if (step instanceof HighlightNodeStep s) {
            highlightedValues.clear();

            // If your HighlightNodeStep accessor is different, change this line:
            int value = s.nodeValue();

            highlightedValues.add(value);
            opLabel.setText("Current op: Highlight node " + value);
        } else {
            opLabel.setText("Current op: " + step.getClass().getSimpleName());
        }
    }

    private int lastHighlightedValue(List<AlgoStep> steps) {
        for (int i = steps.size() - 1; i >= 0; i--) {
            AlgoStep step = steps.get(i);
            if (step instanceof HighlightNodeStep s) {
                // If your accessor is different, change here too:
                return s.nodeValue();
            }
        }
        return Integer.MIN_VALUE;
    }

    private String getSearchPseudocode() {
        return """
                cur = root
                while cur != null:
                    visit(cur)
                    if target == cur.value:
                        return FOUND
                    else if target < cur.value:
                        cur = cur.left
                    else:
                        cur = cur.right
                return NOT_FOUND
                """;
    }

    private String getTraversalPseudocode(String traversal) {
        if ("Pre-order".equals(traversal)) {
            return """
                    preOrder(node):
                        if node == null: return
                        visit(node)
                        preOrder(node.left)
                        preOrder(node.right)
                    """;
        } else if ("Post-order".equals(traversal)) {
            return """
                    postOrder(node):
                        if node == null: return
                        postOrder(node.left)
                        postOrder(node.right)
                        visit(node)
                    """;
        } else {
            return """
                    inOrder(node):
                        if node == null: return
                        inOrder(node.left)
                        visit(node)
                        inOrder(node.right)
                    """;
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        renderer.render(gc, tree.getRoot(), highlightedValues, canvas.getWidth(), canvas.getHeight());
    }
}
