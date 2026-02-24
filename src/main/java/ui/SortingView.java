package ui;

import algorithms.sorting.BubbleSortStepper;
import algorithms.sorting.InsertionSortStepper;
import algorithms.sorting.MergeSortStepper;
import algorithms.sorting.QuickSortStepper;
import algorithms.sorting.SelectionSortStepper;
import controller.PlaybackController;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SetValueStep;
import model.steps.SwapStep;
import visualization.ArrayRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingView extends VBox {
    private final Canvas canvas = new Canvas(900, 420);
    private final Label status = new Label("Ready");
    private final Slider speedSlider = new Slider(0.1, 3.0, 1.0);
    private final Spinner<Integer> sizeSpinner = new Spinner<>(10, 200, 40);
    private final ComboBox<String> algorithmBox = new ComboBox<>();

    // Counters
    private final Label comparisonsLabel = new Label("Comparisons: 0");
    private final Label swapsLabel = new Label("Swaps: 0");
    private final Label stepsPlayedLabel = new Label("Steps played: 0");

    // Teaching / info UI
    private final Label currentOperationLabel = new Label("Current op: None");
    private final TextArea pseudocodeArea = new TextArea();

    private int comparisonsCount = 0;
    private int swapsCount = 0;
    private int stepsPlayedCount = 0;

    private int[] currentArray = randomArray(40);
    private List<AlgoStep> currentSteps = new ArrayList<>();
    private final ArrayRenderer renderer = new ArrayRenderer();

    public SortingView(PlaybackController playbackController) {
        setSpacing(10);
        setPadding(new Insets(10));

        algorithmBox.getItems().addAll(
                "Bubble Sort",
                "Insertion Sort",
                "Selection Sort",
                "Merge Sort",
                "Quick Sort"
        );
        algorithmBox.getSelectionModel().selectFirst();

        pseudocodeArea.setEditable(false);
        pseudocodeArea.setWrapText(false);
        pseudocodeArea.setPrefRowCount(16);
        pseudocodeArea.setPrefColumnCount(34);
        pseudocodeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 12px;");
        pseudocodeArea.setText(getPseudocodeFor(algorithmBox.getValue()));

        Button randomizeBtn = new Button("Randomize");
        Button generateStepsBtn = new Button("Generate Steps");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stepBtn = new Button("Step");
        Button resetBtn = new Button("Reset");

        randomizeBtn.setOnAction(e -> {
            currentArray = randomArray(sizeSpinner.getValue());
            currentSteps = new ArrayList<>();
            resetCounters();
            currentOperationLabel.setText("Current op: None");
            playbackController.reset();
            render();
            status.setText("New array generated");
        });

        generateStepsBtn.setOnAction(e -> {
            int[] working = currentArray.clone();
            currentSteps = generateStepsForSelectedAlgorithm(working);

            resetCounters();
            currentOperationLabel.setText("Current op: Ready to play (" + algorithmBox.getValue() + ")");

            playbackController.loadSteps(currentArray, currentSteps, this::renderAndTrack);
            status.setText("Generated " + currentSteps.size() + " steps using " + algorithmBox.getValue());
        });

        playBtn.setOnAction(e -> playbackController.play(speedSlider.getValue()));
        pauseBtn.setOnAction(e -> playbackController.pause());
        stepBtn.setOnAction(e -> playbackController.stepForward());

        resetBtn.setOnAction(e -> {
            playbackController.reset();
            resetCounters();
            currentOperationLabel.setText("Current op: None");
            render();
            status.setText("Reset playback");
        });

        speedSlider.valueProperty().addListener((obs, oldV, newV) ->
                playbackController.setSpeedMultiplier(newV.doubleValue()));

        sizeSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            currentArray = randomArray(newV);
            currentSteps = new ArrayList<>();
            resetCounters();
            currentOperationLabel.setText("Current op: None");
            playbackController.reset();
            render();
            status.setText("Array resized to " + newV);
        });

        algorithmBox.valueProperty().addListener((obs, oldV, newV) -> {
            currentSteps = new ArrayList<>();
            resetCounters();
            currentOperationLabel.setText("Current op: None");
            pseudocodeArea.setText(getPseudocodeFor(newV));
            playbackController.reset();
            render();
            status.setText("Selected: " + newV);
        });

        HBox controls = new HBox(8,
                new Label("Algorithm:"), algorithmBox,
                new Label("Size:"), sizeSpinner,
                new Label("Speed:"), speedSlider,
                randomizeBtn, generateStepsBtn, playBtn, pauseBtn, stepBtn, resetBtn
        );
        HBox.setHgrow(speedSlider, Priority.ALWAYS);

        HBox counters = new HBox(20, comparisonsLabel, swapsLabel, stepsPlayedLabel);

        VBox leftPanel = new VBox(10, counters, canvas, status);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        Label pseudoTitle = new Label("Pseudocode");
        pseudoTitle.setStyle("-fx-font-weight: bold;");

        Label opTitle = new Label("Current Operation");
        opTitle.setStyle("-fx-font-weight: bold;");

        VBox rightPanel = new VBox(8,
                opTitle,
                currentOperationLabel,
                new Separator(),
                pseudoTitle,
                pseudocodeArea
        );
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

        HBox mainContent = new HBox(12, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        getChildren().addAll(controls, mainContent);

        render();
    }

    private List<AlgoStep> generateStepsForSelectedAlgorithm(int[] working) {
        String selected = algorithmBox.getValue();

        if ("Insertion Sort".equals(selected)) {
            return InsertionSortStepper.generateSteps(working);
        } else if ("Selection Sort".equals(selected)) {
            return SelectionSortStepper.generateSteps(working);
        } else if ("Merge Sort".equals(selected)) {
            return MergeSortStepper.generateSteps(working);
        } else if ("Quick Sort".equals(selected)) {
            return QuickSortStepper.generateSteps(working);
        } else {
            return BubbleSortStepper.generateSteps(working);
        }
    }

    private void resetCounters() {
        comparisonsCount = 0;
        swapsCount = 0;
        stepsPlayedCount = 0;
        refreshCounterLabels();
    }

    private void refreshCounterLabels() {
        comparisonsLabel.setText("Comparisons: " + comparisonsCount);
        swapsLabel.setText("Swaps: " + swapsCount);
        stepsPlayedLabel.setText("Steps played: " + stepsPlayedCount);
    }

    private void render() {
        renderWithArrayState(currentArray.clone(), null);
    }

    /**
     * Callback used by PlaybackController during replay.
     * Counters are updated based on the step the user actually watched.
     */
    private void renderAndTrack(int[] array, AlgoStep lastStep) {
        if (lastStep != null) {
            stepsPlayedCount++;

            if (lastStep instanceof CompareStep) {
                comparisonsCount++;
            } else if (lastStep instanceof SwapStep) {
                swapsCount++;
            }

            currentOperationLabel.setText("Current op: " + describeStep(lastStep));
            refreshCounterLabels();
        }

        renderWithArrayState(array, lastStep);
    }

    private String describeStep(AlgoStep step) {
        if (step instanceof CompareStep c) {
            return "Compare indices " + c.i() + " and " + c.j();
        } else if (step instanceof SwapStep s) {
            return "Swap indices " + s.i() + " and " + s.j();
        } else if (step instanceof SetValueStep w) {
            return "Write index " + w.index() + " = " + w.value();
        } else if (step instanceof MarkSortedStep m) {
            return "Mark sorted index " + m.index();
        } else {
            return step.getClass().getSimpleName();
        }
    }

    private void renderWithArrayState(int[] array, AlgoStep lastStep) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        renderer.render(gc, array, canvas.getWidth(), canvas.getHeight(), lastStep);
    }

    private String getPseudocodeFor(String algorithmName) {
        if ("Insertion Sort".equals(algorithmName)) {
            return """
                    for i = 1 to n-1:
                        key = A[i]
                        j = i - 1
                        while j >= 0 and A[j] > key:
                            A[j+1] = A[j]
                            j = j - 1
                        A[j+1] = key
                    """;
        } else if ("Selection Sort".equals(algorithmName)) {
            return """
                    for i = 0 to n-2:
                        minIndex = i
                        for j = i+1 to n-1:
                            if A[j] < A[minIndex]:
                                minIndex = j
                        swap A[i], A[minIndex]
                    """;
        } else if ("Merge Sort".equals(algorithmName)) {
            return """
                    mergeSort(left, right):
                        if left >= right: return
                        mid = (left + right) / 2
                        mergeSort(left, mid)
                        mergeSort(mid+1, right)
                        merge(left, mid, right)

                    merge(left, mid, right):
                        compare left-half and right-half
                        write merged values back into array
                    """;
        } else if ("Quick Sort".equals(algorithmName)) {
            return """
                    quickSort(low, high):
                        if low >= high: return
                        p = partition(low, high)
                        quickSort(low, p-1)
                        quickSort(p+1, high)

                    partition(low, high):
                        pivot = A[high]
                        i = low - 1
                        for j = low to high-1:
                            if A[j] <= pivot:
                                i++
                                swap A[i], A[j]
                        swap A[i+1], A[high]
                        return i + 1
                    """;
        } else { // Bubble Sort default
            return """
                    repeat until no swaps:
                        swapped = false
                        for i = 0 to n-2:
                            if A[i] > A[i+1]:
                                swap A[i], A[i+1]
                                swapped = true
                    """;
        }
    }

    private static int[] randomArray(int size) {
        Random random = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = 10 + random.nextInt(390);
        }
        return arr;
    }
}
