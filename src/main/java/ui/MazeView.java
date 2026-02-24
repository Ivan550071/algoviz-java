package ui;

import algorithms.recursion.MazeBfsStepper;
import algorithms.recursion.MazeDfsStepper;
import controller.PlaybackController;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.maze.MazeGrid;
import model.steps.AlgoStep;
import model.steps.BacktrackCellStep;
import model.steps.PathCellStep;
import model.steps.VisitCellStep;
import visualization.MazeRenderer;

import java.util.ArrayList;
import java.util.List;

public class MazeView extends VBox {
    private final Canvas canvas = new Canvas(900, 500);

    private final Spinner<Integer> rowsSpinner = new Spinner<>(5, 60, 20);
    private final Spinner<Integer> colsSpinner = new Spinner<>(5, 60, 30);
    private final Slider speedSlider = new Slider(0.1, 3.0, 1.0);
    private final Slider wallSlider = new Slider(0.05, 0.45, 0.25);
    private final ComboBox<String> solverBox = new ComboBox<>();

    private final Label status = new Label("Ready");
    private final Label currentOpLabel = new Label("Current op: None");
    private final Label stepsLabel = new Label("Steps played: 0");

    private final MazeRenderer renderer = new MazeRenderer();

    private MazeGrid grid = MazeGrid.randomGrid(20, 30, 0.25);
    private List<AlgoStep> currentSteps = new ArrayList<>();

    // Playback-rendered state (persistent overlays)
    private boolean[][] visitedState;
    private boolean[][] backtrackedState;
    private boolean[][] pathState;
    private int stepsPlayed = 0;

    public MazeView(PlaybackController playbackController) {
        setSpacing(10);
        setPadding(new Insets(10));

        wallSlider.setShowTickLabels(true);
        wallSlider.setShowTickMarks(true);
        wallSlider.setMajorTickUnit(0.10);
        wallSlider.setMinorTickCount(4);

        solverBox.getItems().addAll("DFS", "BFS");
        solverBox.getSelectionModel().select("DFS");

        Button randomizeBtn = new Button("Randomize Grid");
        Button generateBtn = new Button("Generate Steps");
        Button playBtn = new Button("Play");
        Button pauseBtn = new Button("Pause");
        Button stepBtn = new Button("Step");
        Button resetBtn = new Button("Reset");

        randomizeBtn.setOnAction(e -> {
            grid = MazeGrid.randomGrid(rowsSpinner.getValue(), colsSpinner.getValue(), wallSlider.getValue());
            currentSteps = new ArrayList<>();
            resetOverlayState();
            playbackController.reset();
            render();
            status.setText("Randomized grid (" + grid.getRows() + "x" + grid.getCols() + ")");
        });

        generateBtn.setOnAction(e -> {
            resetOverlayState();

            String solver = solverBox.getValue();
            if ("BFS".equals(solver)) {
                currentSteps = MazeBfsStepper.generateSteps(grid);
            } else {
                currentSteps = MazeDfsStepper.generateSteps(grid);
            }

            // PlaybackController is array-oriented; we ignore the array for maze playback.
            int[] dummyState = new int[0];

            playbackController.loadSteps(dummyState, currentSteps, (ignoredArray, lastStep) -> {
                applyMazeStep(lastStep);
                render();
            });

            status.setText("Generated " + currentSteps.size() + " " + solver + " steps");
        });

        playBtn.setOnAction(e -> playbackController.play(speedSlider.getValue()));
        pauseBtn.setOnAction(e -> playbackController.pause());
        stepBtn.setOnAction(e -> playbackController.stepForward());

        resetBtn.setOnAction(e -> {
            playbackController.reset();
            resetOverlayState();
            render();
            status.setText("Reset playback");
        });

        speedSlider.valueProperty().addListener((obs, oldV, newV) ->
                playbackController.setSpeedMultiplier(newV.doubleValue()));

        rowsSpinner.valueProperty().addListener((obs, oldV, newV) -> regenerateGridAndReset(playbackController));
        colsSpinner.valueProperty().addListener((obs, oldV, newV) -> regenerateGridAndReset(playbackController));

        wallSlider.valueProperty().addListener((obs, oldV, newV) ->
                status.setText(String.format("Wall probability: %.2f", newV.doubleValue())));

        solverBox.valueProperty().addListener((obs, oldV, newV) -> {
            currentSteps = new ArrayList<>();
            resetOverlayState();
            playbackController.reset();
            render();
            status.setText("Selected solver: " + newV);
        });

        HBox controls = new HBox(8,
                new Label("Rows:"), rowsSpinner,
                new Label("Cols:"), colsSpinner,
                new Label("Walls:"), wallSlider,
                new Label("Solver:"), solverBox,
                new Label("Speed:"), speedSlider,
                randomizeBtn, generateBtn, playBtn, pauseBtn, stepBtn, resetBtn
        );
        HBox.setHgrow(speedSlider, Priority.ALWAYS);
        HBox.setHgrow(wallSlider, Priority.ALWAYS);

        HBox info = new HBox(20, currentOpLabel, stepsLabel);

        getChildren().addAll(controls, info, canvas, status);

        resetOverlayState();
        render();
    }

    private void regenerateGridAndReset(PlaybackController playbackController) {
        grid = MazeGrid.randomGrid(rowsSpinner.getValue(), colsSpinner.getValue(), wallSlider.getValue());
        currentSteps = new ArrayList<>();
        resetOverlayState();
        playbackController.reset();
        render();
        status.setText("Grid resized to " + grid.getRows() + "x" + grid.getCols());
    }

    private void resetOverlayState() {
        visitedState = new boolean[grid.getRows()][grid.getCols()];
        backtrackedState = new boolean[grid.getRows()][grid.getCols()];
        pathState = new boolean[grid.getRows()][grid.getCols()];
        stepsPlayed = 0;
        stepsLabel.setText("Steps played: 0");
        currentOpLabel.setText("Current op: None");
    }

    private void applyMazeStep(AlgoStep step) {
        if (step == null) return;

        stepsPlayed++;
        stepsLabel.setText("Steps played: " + stepsPlayed);

        if (step instanceof VisitCellStep s) {
            visitedState[s.row()][s.col()] = true;
            currentOpLabel.setText("Current op: Visit (" + s.row() + ", " + s.col() + ")");
        } else if (step instanceof BacktrackCellStep s) {
            backtrackedState[s.row()][s.col()] = true;
            currentOpLabel.setText("Current op: Backtrack (" + s.row() + ", " + s.col() + ")");
        } else if (step instanceof PathCellStep s) {
            pathState[s.row()][s.col()] = true;
            currentOpLabel.setText("Current op: Path (" + s.row() + ", " + s.col() + ")");
        } else {
            currentOpLabel.setText("Current op: " + step.getClass().getSimpleName());
        }
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        renderer.render(gc, grid, visitedState, backtrackedState, pathState, canvas.getWidth(), canvas.getHeight());
    }
}
