package model.steps;

public record BacktrackCellStep(int row, int col) implements AlgoStep {
    @Override
    public StepType type() { return StepType.BACKTRACK_CELL; }
}
