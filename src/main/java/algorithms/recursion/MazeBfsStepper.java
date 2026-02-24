package algorithms.recursion;

import model.maze.MazeGrid;
import model.steps.AlgoStep;
import model.steps.BacktrackCellStep;
import model.steps.PathCellStep;
import model.steps.VisitCellStep;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class MazeBfsStepper {
    private MazeBfsStepper() {}

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

        int sr = grid.getStartRow();
        int sc = grid.getStartCol();
        int er = grid.getEndRow();
        int ec = grid.getEndCol();

        Deque<int[]> queue = new ArrayDeque<>();
        visited[sr][sc] = true;
        queue.addLast(new int[]{sr, sc});
        steps.add(new VisitCellStep(sr, sc));

        boolean found = false;

        // right, down, left, up (same visual order as DFS)
        int[][] dirs = {
                {0, 1},
                {1, 0},
                {0, -1},
                {-1, 0}
        };

        while (!queue.isEmpty()) {
            int[] cur = queue.removeFirst();
            int r = cur[0];
            int c = cur[1];

            if (r == er && c == ec) {
                found = true;
                break;
            }

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];

                if (!grid.inBounds(nr, nc) || grid.isWall(nr, nc) || visited[nr][nc]) {
                    continue;
                }

                visited[nr][nc] = true;
                parentR[nr][nc] = r;
                parentC[nr][nc] = c;

                queue.addLast(new int[]{nr, nc});
                steps.add(new VisitCellStep(nr, nc));
            }

            // BFS doesn't "backtrack" recursively, but emitting this makes the animation
            // more informative and reuses existing orange overlay nicely.
            if (!(r == sr && c == sc)) {
                steps.add(new BacktrackCellStep(r, c));
            }
        }

        if (found) {
            List<int[]> path = new ArrayList<>();
            int r = er;
            int c = ec;

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
}
