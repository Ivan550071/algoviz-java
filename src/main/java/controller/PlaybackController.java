package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.SetValueStep;
import model.steps.SwapStep;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class PlaybackController {
    private final Timeline timeline = new Timeline();
    private double speedMultiplier = 1.0;

    private int[] initialArray = new int[0];
    private int[] currentArray = new int[0];
    private List<AlgoStep> steps = new ArrayList<>();
    private int pointer = 0;
    private BiConsumer<int[], AlgoStep> renderCallback;

    public PlaybackController() {
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(120), e -> stepForward()));
    }

    public void loadSteps(int[] baseArray, List<AlgoStep> steps, BiConsumer<int[], AlgoStep> renderCallback) {
        pause();
        this.initialArray = baseArray.clone();
        this.currentArray = baseArray.clone();
        this.steps = new ArrayList<>(steps);
        this.pointer = 0;
        this.renderCallback = renderCallback;
        render(null);
    }

    public void play(double speedMultiplier) {
        setSpeedMultiplier(speedMultiplier);
        if (!steps.isEmpty()) {
            timeline.play();
        }
    }

    public void pause() {
        timeline.pause();
    }

    public void reset() {
        pause();
        if (initialArray.length > 0) {
            currentArray = initialArray.clone();
        }
        pointer = 0;
        render(null);
    }

    public void stepForward() {
        if (pointer >= steps.size()) {
            pause();
            return;
        }
        AlgoStep step = steps.get(pointer++);
        apply(step);
        render(step);
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = Math.max(0.1, speedMultiplier);
        double baseMillis = 120;
        timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(baseMillis / this.speedMultiplier), e -> stepForward()));
    }

    private void apply(AlgoStep step) {
        if (step instanceof SwapStep s) {
            int tmp = currentArray[s.i()];
            currentArray[s.i()] = currentArray[s.j()];
            currentArray[s.j()] = tmp;
        } else if (step instanceof SetValueStep s) {
            currentArray[s.index()] = s.value();
        } else if (step instanceof CompareStep) {
            // Visual-only step.
        }
    }

    private void render(AlgoStep step) {
        if (renderCallback != null) {
            renderCallback.accept(currentArray.clone(), step);
        }
    }
}
