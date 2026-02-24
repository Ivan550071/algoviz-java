package algorithms.trees;

import model.steps.AlgoStep;
import model.steps.HighlightNodeStep;
import model.tree.BstTree;
import model.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public final class BstStepper {
    private BstStepper() {}

    public static List<AlgoStep> generateSearchSteps(BstTree tree, int target) {
        List<AlgoStep> steps = new ArrayList<>();

        TreeNode cur = tree.getRoot();
        while (cur != null) {
            steps.add(new HighlightNodeStep(cur.value));

            if (target == cur.value) {
                break;
            } else if (target < cur.value) {
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }

        return steps;
    }

    public static List<AlgoStep> generateInOrderTraversalSteps(BstTree tree) {
        List<AlgoStep> steps = new ArrayList<>();
        inOrder(tree.getRoot(), steps);
        return steps;
    }

    public static List<AlgoStep> generatePreOrderTraversalSteps(BstTree tree) {
        List<AlgoStep> steps = new ArrayList<>();
        preOrder(tree.getRoot(), steps);
        return steps;
    }

    public static List<AlgoStep> generatePostOrderTraversalSteps(BstTree tree) {
        List<AlgoStep> steps = new ArrayList<>();
        postOrder(tree.getRoot(), steps);
        return steps;
    }

    private static void inOrder(TreeNode node, List<AlgoStep> steps) {
        if (node == null) return;
        inOrder(node.left, steps);
        steps.add(new HighlightNodeStep(node.value));
        inOrder(node.right, steps);
    }

    private static void preOrder(TreeNode node, List<AlgoStep> steps) {
        if (node == null) return;
        steps.add(new HighlightNodeStep(node.value));
        preOrder(node.left, steps);
        preOrder(node.right, steps);
    }

    private static void postOrder(TreeNode node, List<AlgoStep> steps) {
        if (node == null) return;
        postOrder(node.left, steps);
        postOrder(node.right, steps);
        steps.add(new HighlightNodeStep(node.value));
    }
}
