package model.steps;

public record CompareStep(int i, int j) implements AlgoStep {
    @Override
    public StepType type() { return StepType.COMPARE; }
}
