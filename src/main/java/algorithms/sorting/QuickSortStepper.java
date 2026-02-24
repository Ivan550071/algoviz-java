package algorithms.sorting;

import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SwapStep;

import java.util.ArrayList;
import java.util.List;

public final class QuickSortStepper {
    private QuickSortStepper() {}

    public static List<AlgoStep> generateSteps(int[] arr) {
        List<AlgoStep> steps = new ArrayList<>();
        if (arr == null || arr.length == 0) return steps;

        quickSort(arr, 0, arr.length - 1, steps);

        // Mark all sorted at the end for current renderer simplicity
        for (int i = 0; i < arr.length; i++) {
            steps.add(new MarkSortedStep(i));
        }

        return steps;
    }

    private static void quickSort(int[] arr, int low, int high, List<AlgoStep> steps) {
        if (low >= high) return;

        int pivotIndex = partition(arr, low, high, steps);
        quickSort(arr, low, pivotIndex - 1, steps);
        quickSort(arr, pivotIndex + 1, high, steps);
    }

    /**
     * Lomuto partition scheme (pivot = arr[high]).
     */
    private static int partition(int[] arr, int low, int high, List<AlgoStep> steps) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // Compare arr[j] with pivot (represented by high index)
            steps.add(new CompareStep(j, high));

            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                    steps.add(new SwapStep(i, j));
                }
            }
        }

        int pivotPos = i + 1;
        if (pivotPos != high) {
            swap(arr, pivotPos, high);
            steps.add(new SwapStep(pivotPos, high));
        }

        steps.add(new MarkSortedStep(pivotPos));

        return pivotPos;
    }

    private static void swap(int[] arr, int a, int b) {
        int tmp = arr[a];
        arr[a] = arr[b];
        arr[b] = tmp;
    }
}
