package controller;

import model.steps.AlgoStep;

import java.util.List;

public interface AlgorithmRunner<TInput> {
    List<AlgoStep> generateSteps(TInput input);
}
