package model.steps;

public record HighlightNodeStep(int nodeValue) implements AlgoStep {
    @Override
    public StepType type() { return StepType.HIGHLIGHT_NODE; }
}
