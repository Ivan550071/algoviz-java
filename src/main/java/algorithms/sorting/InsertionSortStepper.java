package algorithms.sorting;

import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SetValueStep;

import java.util.ArrayList;
import java.util.List;

public final class InsertionSortStepper {
    private InsertionSortStepper() {}

    public static List<AlgoStep> generateSteps(int[] arr) {
        List<AlgoStep> steps = new ArrayList<>();
        int n = arr.length;

        if (n == 0) return steps;

        // First element is trivially "sorted" in insertion sort context.
        steps.add(new MarkSortedStep(0));

        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;

            // Shift larger elements to the right
            while (j >= 0) {
                steps.add(new CompareStep(j, i)); // compare arr[j] with current key-origin index i
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    steps.add(new SetValueStep(j + 1, arr[j]));
                    j--;
                } else {
                    break;
                }
            }

            arr[j + 1] = key;
            steps.add(new SetValueStep(j + 1, key));

            // Mark current sorted prefix [0..i]
            steps.add(new MarkSortedStep(i));
        }

        return steps;
    }
}
