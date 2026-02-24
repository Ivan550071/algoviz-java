package visualization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.tree.TreeNode;

import java.util.Set;

public class TreeRenderer {
    private static final double NODE_RADIUS = 18;
    private static final double LEVEL_GAP = 72;

    public void render(GraphicsContext gc, TreeNode root, Set<Integer> highlightedValues, double width, double height) {
        gc.setFill(Color.web("#111827"));
        gc.fillRect(0, 0, width, height);

        if (root == null) {
            gc.setFill(Color.web("#d1d5db"));
            gc.fillText("Tree is empty", 20, 30);
            return;
        }

        drawNode(gc, root, width / 2.0, 55, width / 4.0, highlightedValues);
    }

    private void drawNode(GraphicsContext gc, TreeNode node, double x, double y, double xOffset, Set<Integer> highlightedValues) {
        if (node == null) return;

        double childY = y + LEVEL_GAP;
        double nextOffset = Math.max(24, xOffset / 2.0);

        if (node.left != null) {
            double childX = x - xOffset;
            gc.setStroke(Color.web("#9ca3af"));
            gc.setLineWidth(2);
            gc.strokeLine(x, y, childX, childY);
            drawNode(gc, node.left, childX, childY, nextOffset, highlightedValues);
        }

        if (node.right != null) {
            double childX = x + xOffset;
            gc.setStroke(Color.web("#9ca3af"));
            gc.setLineWidth(2);
            gc.strokeLine(x, y, childX, childY);
            drawNode(gc, node.right, childX, childY, nextOffset, highlightedValues);
        }

        boolean highlighted = highlightedValues != null && highlightedValues.contains(node.value);

        gc.setFill(highlighted ? Color.web("#f59e0b") : Color.web("#60a5fa"));
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setStroke(Color.web("#e5e7eb"));
        gc.setLineWidth(1.5);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(Color.WHITE);
        String text = String.valueOf(node.value);
        gc.fillText(text, x - (text.length() * 3.5), y + 4);
    }
}
