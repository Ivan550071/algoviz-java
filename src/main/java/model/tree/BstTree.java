package model.tree;

import java.util.ArrayList;
import java.util.List;

public class BstTree {
    private TreeNode root;

    public TreeNode getRoot() {
        return root;
    }

    public void clear() {
        root = null;
    }

    public boolean insert(int value) {
        if (root == null) {
            root = new TreeNode(value);
            return true;
        }

        TreeNode cur = root;
        while (true) {
            if (value == cur.value) {
                return false; // ignore duplicates
            } else if (value < cur.value) {
                if (cur.left == null) {
                    cur.left = new TreeNode(value);
                    return true;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new TreeNode(value);
                    return true;
                }
                cur = cur.right;
            }
        }
    }

    public List<Integer> inOrderValues() {
        List<Integer> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }

    private void inOrder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inOrder(node.left, out);
        out.add(node.value);
        inOrder(node.right, out);
    }
}
