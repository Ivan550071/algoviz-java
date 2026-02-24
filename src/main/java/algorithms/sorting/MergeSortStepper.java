package algorithms.sorting;

import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SetValueStep;

import java.util.ArrayList;
import java.util.List;

public final class MergeSortStepper {
    private MergeSortStepper() {}

    public static List<AlgoStep> generateSteps(int[] arr) {
        List<AlgoStep> steps = new ArrayList<>();
        if (arr == null || arr.length == 0) return steps;

        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1, steps);

        // Mark all as sorted at the end (simple + works with current renderer)
        for (int i = 0; i < arr.length; i++) {
            steps.add(new MarkSortedStep(i));
        }

        return steps;
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right, List<AlgoStep> steps) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;

        mergeSort(arr, temp, left, mid, steps);
        mergeSort(arr, temp, mid + 1, right, steps);
        merge(arr, temp, left, mid, right, steps);
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right, List<AlgoStep> steps) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            steps.add(new CompareStep(i, j));
            if (arr[i] <= arr[j]) {
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = arr[i++];
        }

        while (j <= right) {
            temp[k++] = arr[j++];
        }

        // Copy back to arr and emit writes for visualization
        for (int idx = left; idx <= right; idx++) {
            arr[idx] = temp[idx];
            steps.add(new SetValueStep(idx, arr[idx]));
        }
    }
}
