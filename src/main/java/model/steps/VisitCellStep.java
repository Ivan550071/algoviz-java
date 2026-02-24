package model.steps;

public record VisitCellStep(int row, int col) implements AlgoStep {
    @Override
    public StepType type() { return StepType.VISIT_CELL; }
}
