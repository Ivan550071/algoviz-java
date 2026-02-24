package model.steps;

public record SwapStep(int i, int j) implements AlgoStep {
    @Override
    public StepType type() { return StepType.SWAP; }
}
