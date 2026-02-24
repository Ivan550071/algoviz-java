package model.maze;

import java.util.Random;

public class MazeGrid {
    private final int rows;
    private final int cols;
    private final boolean[][] walls;

    private final int startRow;
    private final int startCol;
    private final int endRow;
    private final int endCol;

    public MazeGrid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.walls = new boolean[rows][cols];

        this.startRow = 0;
        this.startCol = 0;
        this.endRow = rows - 1;
        this.endCol = cols - 1;
    }

    public static MazeGrid randomGrid(int rows, int cols, double wallProbability) {
        MazeGrid grid = new MazeGrid(rows, cols);
        Random random = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // keep start/end open
                if ((r == grid.startRow && c == grid.startCol) ||
                        (r == grid.endRow && c == grid.endCol)) {
                    grid.walls[r][c] = false;
                } else {
                    grid.walls[r][c] = random.nextDouble() < wallProbability;
                }
            }
        }

        return grid;
    }

    public boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public boolean isWall(int r, int c) {
        return walls[r][c];
    }

    public void setWall(int r, int c, boolean value) {
        if (inBounds(r, c)) {
            walls[r][c] = value;
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public int getStartRow() { return startRow; }
    public int getStartCol() { return startCol; }

    public int getEndRow() { return endRow; }
    public int getEndCol() { return endCol; }

    public boolean[][] copyWalls() {
        boolean[][] copy = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(walls[r], 0, copy[r], 0, cols);
        }
        return copy;
    }
}
