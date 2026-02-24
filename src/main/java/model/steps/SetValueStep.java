package model.steps;

public record SetValueStep(int index, int value) implements AlgoStep {
    @Override
    public StepType type() { return StepType.SET_VALUE; }
}
