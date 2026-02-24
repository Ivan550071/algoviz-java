package model.steps;

public sealed interface AlgoStep permits
        CompareStep,
        SwapStep,
        SetValueStep,
        MarkSortedStep,
        VisitCellStep,
        BacktrackCellStep,
        PathCellStep,
        HighlightNodeStep {

    StepType type();
}
