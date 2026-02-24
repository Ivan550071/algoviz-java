package algorithms.sorting;

import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SwapStep;

import java.util.ArrayList;
import java.util.List;

public final class SelectionSortStepper {
    private SelectionSortStepper() {}

    public static List<AlgoStep> generateSteps(int[] arr) {
        List<AlgoStep> steps = new ArrayList<>();
        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;

            for (int j = i + 1; j < n; j++) {
                steps.add(new CompareStep(minIdx, j));
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }

            if (minIdx != i) {
                int tmp = arr[i];
                arr[i] = arr[minIdx];
                arr[minIdx] = tmp;
                steps.add(new SwapStep(i, minIdx));
            }

            steps.add(new MarkSortedStep(i));
        }

        if (n > 0) {
            steps.add(new MarkSortedStep(n - 1));
        }

        return steps;
    }
}
