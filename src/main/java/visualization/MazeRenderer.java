package visualization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.maze.MazeGrid;

public class MazeRenderer {

    public void render(
            GraphicsContext gc,
            MazeGrid grid,
            boolean[][] visited,
            boolean[][] backtracked,
            boolean[][] path,
            double width,
            double height
    ) {
        gc.setFill(Color.web("#111827"));
        gc.fillRect(0, 0, width, height);

        if (grid == null) return;

        int rows = grid.getRows();
        int cols = grid.getCols();

        double cellW = width / cols;
        double cellH = height / rows;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color fill;

                if (grid.isWall(r, c)) {
                    fill = Color.web("#1f2937"); // wall
                } else {
                    fill = Color.web("#f3f4f6"); // open
                }

                // overlays (order matters)
                if (!grid.isWall(r, c) && visited != null && visited[r][c]) {
                    fill = Color.web("#93c5fd"); // visited
                }
                if (!grid.isWall(r, c) && backtracked != null && backtracked[r][c]) {
                    fill = Color.web("#fdba74"); // backtracked
                }
                if (!grid.isWall(r, c) && path != null && path[r][c]) {
                    fill = Color.web("#4ade80"); // final path
                }

                // Start / End override everything for clarity
                if (r == grid.getStartRow() && c == grid.getStartCol()) {
                    fill = Color.web("#22c55e");
                }
                if (r == grid.getEndRow() && c == grid.getEndCol()) {
                    fill = Color.web("#ef4444");
                }

                double x = c * cellW;
                double y = r * cellH;

                gc.setFill(fill);
                gc.fillRect(x, y, cellW, cellH);

                gc.setStroke(Color.web("#d1d5db"));
                gc.setLineWidth(0.5);
                gc.strokeRect(x, y, cellW, cellH);
            }
        }
    }
}
