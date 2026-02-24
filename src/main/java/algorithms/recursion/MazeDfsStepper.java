package algorithms.recursion;

import model.maze.MazeGrid;
import model.steps.AlgoStep;
import model.steps.BacktrackCellStep;
import model.steps.PathCellStep;
import model.steps.VisitCellStep;

import java.util.ArrayList;
import java.util.List;

public final class MazeDfsStepper {
    private MazeDfsStepper() {}

    public static List<AlgoStep> generateSteps(MazeGrid grid) {
        List<AlgoStep> steps = new ArrayList<>();

        int rows = grid.getRows();
        int cols = grid.getCols();

        boolean[][] visited = new boolean[rows][cols];
        int[][] parentR = new int[rows][cols];
        int[][] parentC = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                parentR[r][c] = -1;
                parentC[r][c] = -1;
            }
        }

        boolean found = dfs(
                grid,
                grid.getStartRow(), grid.getStartCol(),
                visited,
                parentR, parentC,
                steps
        );

        if (found) {
            // Reconstruct path from end -> start, then emit in start -> end order
            List<int[]> path = new ArrayList<>();
            int r = grid.getEndRow();
            int c = grid.getEndCol();

            while (r != -1 && c != -1) {
                path.add(new int[]{r, c});
                int pr = parentR[r][c];
                int pc = parentC[r][c];
                r = pr;
                c = pc;
            }

            for (int i = path.size() - 1; i >= 0; i--) {
                int[] cell = path.get(i);
                steps.add(new PathCellStep(cell[0], cell[1]));
            }
        }

        return steps;
    }

    private static boolean dfs(
            MazeGrid grid,
            int r, int c,
            boolean[][] visited,
            int[][] parentR,
            int[][] parentC,
            List<AlgoStep> steps
    ) {
        if (!grid.inBounds(r, c) || grid.isWall(r, c) || visited[r][c]) {
            return false;
        }

        visited[r][c] = true;
        steps.add(new VisitCellStep(r, c));

        if (r == grid.getEndRow() && c == grid.getEndCol()) {
            return true;
        }

        // Direction order: right, down, left, up (nice visual sweep)
        int[][] dirs = {
                {0, 1},
                {1, 0},
                {0, -1},
                {-1, 0}
        };

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = c + d[1];

            if (grid.inBounds(nr, nc) && !grid.isWall(nr, nc) && !visited[nr][nc]) {
                parentR[nr][nc] = r;
                parentC[nr][nc] = c;

                if (dfs(grid, nr, nc, visited, parentR, parentC, steps)) {
                    return true;
                }
            }
        }

        steps.add(new BacktrackCellStep(r, c));
        return false;
    }
}
