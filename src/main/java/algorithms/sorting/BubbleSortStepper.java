package algorithms.sorting;

import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SwapStep;

import java.util.ArrayList;
import java.util.List;

public final class BubbleSortStepper {
    private BubbleSortStepper() {}

    public static List<AlgoStep> generateSteps(int[] arr) {
        List<AlgoStep> steps = new ArrayList<>();
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                steps.add(new CompareStep(j, j + 1));
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    steps.add(new SwapStep(j, j + 1));
                    swapped = true;
                }
            }
            steps.add(new MarkSortedStep(n - 1 - i));
            if (!swapped) {
                break;
            }
        }
        if (n > 0) {
            steps.add(new MarkSortedStep(0));
        }
        return steps;
    }
}
