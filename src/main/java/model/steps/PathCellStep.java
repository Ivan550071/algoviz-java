package model.steps;

public record PathCellStep(int row, int col) implements AlgoStep {
    @Override
    public StepType type() { return StepType.PATH_CELL; }
}
