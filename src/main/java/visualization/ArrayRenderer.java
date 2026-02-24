package visualization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.steps.AlgoStep;
import model.steps.CompareStep;
import model.steps.MarkSortedStep;
import model.steps.SwapStep;

public class ArrayRenderer {
    public void render(GraphicsContext gc, int[] arr, double width, double height, AlgoStep lastStep) {
        gc.setFill(Color.web("#111827"));
        gc.fillRect(0, 0, width, height);

        if (arr == null || arr.length == 0) return;

        double barWidth = Math.max(2, width / arr.length);
        int max = 1;
        for (int v : arr) max = Math.max(max, v);

        int hiA = -1, hiB = -1;
        int sortedIdx = -1;

        if (lastStep instanceof CompareStep c) {
            hiA = c.i();
            hiB = c.j();
        } else if (lastStep instanceof SwapStep s) {
            hiA = s.i();
            hiB = s.j();
        } else if (lastStep instanceof MarkSortedStep m) {
            sortedIdx = m.index();
        }

        for (int i = 0; i < arr.length; i++) {
            double barHeight = (arr[i] / (double) max) * (height - 20);
            double x = i * barWidth;
            double y = height - barHeight;

            if (i == hiA || i == hiB) {
                gc.setFill(Color.web("#f59e0b")); // compare/swap highlight
            } else if (i == sortedIdx) {
                gc.setFill(Color.web("#22c55e")); // sorted marker
            } else {
                gc.setFill(Color.web("#60a5fa"));
            }

            gc.fillRect(x, y, Math.max(1, barWidth - 1), barHeight);
        }
    }
}
