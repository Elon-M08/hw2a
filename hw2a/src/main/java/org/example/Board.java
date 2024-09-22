package org.example;

public class Board {
    private int[][] grid;
    private Worker[][] workers;

    public Board() {
        // Initialize the grid to all zeros (0 = empty).
        grid = new int[5][5];
        // Initialize worker positions as null (no worker initially).
        workers = new Worker[5][5];
    }

    // Check if a field is occupied (by a worker or a dome).
    public boolean isOccupied(int x, int y) {
        return workers[x][y] != null || grid[x][y] == 4;  // 4 means a dome
    }

    // Get the current height of a tower at a specific field.
    public int getTowerHeight(int x, int y) {
        return grid[x][y];
    }

    // Build a block or a dome on an adjacent field.
    public boolean build(int x, int y) {
        if (isOccupied(x, y)) {
            return false;  // Cannot build on an occupied field.
        }

        // Build a block if the tower is less than 3, otherwise build a dome.
        if (grid[x][y] < 3) {
            grid[x][y]++;
        } else if (grid[x][y] == 3) {
            grid[x][y] = 4;  // Build a dome on a level-3 tower.
        }
        return true;
    }

    // Check if a move is valid (adjacent and max one level higher).
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Check if the target position is within bounds.
        if (toX < 0 || toX >= 5 || toY < 0 || toY >= 5) {
            return false;
        }

        // Check if the target field is adjacent.
        if (Math.abs(fromX - toX) > 1 || Math.abs(fromY - toY) > 1) {
            return false;
        }

        // Check if the target field is unoccupied.
        if (isOccupied(toX, toY)) {
            return false;
        }

        // Check if the worker can climb no more than one level.
        if (grid[toX][toY] - grid[fromX][fromY] > 1) {
            return false;
        }

        return true;
    }

    // Place a worker on the board.
    public void placeWorker(int x, int y, Worker worker) {
        if (!isOccupied(x, y)) {
            workers[x][y] = worker;
        }
    }

    // Move a worker from one position to another.
    public void moveWorker(int fromX, int fromY, int toX, int toY) {
        if (isValidMove(fromX, fromY, toX, toY)) {
            workers[toX][toY] = workers[fromX][fromY];
            workers[fromX][fromY] = null;
        }
    }
}

