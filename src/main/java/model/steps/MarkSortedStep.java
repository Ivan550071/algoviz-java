package model.steps;

public record MarkSortedStep(int index) implements AlgoStep {
    @Override
    public StepType type() { return StepType.MARK_SORTED; }
}
