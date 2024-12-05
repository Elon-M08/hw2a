package org.example;

import java.util.Objects;
import java.util.Arrays;

/**
 * The Board class represents a 5x5 game board used in the game Santorini,
 * providing core functionality for managing tower heights, worker positions,
 * and movements according to game rules.
 */
public class Board {
    private static final int BOARD_SIZE = 5;
    private static final int MAX_HEIGHT = 4; // Maximum tower height (including domes)
    private final int[][] grid; // Tower heights
    private final Worker[][] workers; // Worker positions

    /**
     * Constructor to initialize the board with empty towers and no workers.
     */
    public Board() {
        grid = new int[BOARD_SIZE][BOARD_SIZE];
        workers = new Worker[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Checks if the specified position is occupied by a worker or dome.
     *
     * @param x The X-coordinate (0-based).
     * @param y The Y-coordinate (0-based).
     * @return True if the position is occupied; false otherwise.
     */
    public boolean isOccupied(int x, int y) {
        return isWithinBounds(x, y) && (workers[x][y] != null || grid[x][y] == MAX_HEIGHT);
    }

    /**
     * Retrieves the height of the tower at the specified position.
     *
     * @param x The X-coordinate (0-based).
     * @param y The Y-coordinate (0-based).
     * @return The height of the tower at the position.
     * @throws IllegalArgumentException If the position is out of bounds.
     */
    public int getTowerHeight(int x, int y) {
        if (!isWithinBounds(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds.");
        }
        return grid[x][y];
    }

    /**
     * Builds a block or dome at the specified position.
     *
     * @param worker The worker initiating the build.
     * @param x      The X-coordinate (0-based).
     * @param y      The Y-coordinate (0-based).
     * @return True if the build was successful; false otherwise.
     */
    public boolean build( int x, int y) {
        if (!isWithinBounds(x, y)) {
            return false;
        }

        if (isOccupied(x, y)) {
            return false; // Cannot build on an occupied position
        }

        // Increment height or build a dome
        if (grid[x][y] < MAX_HEIGHT - 1) {
            grid[x][y]++;
        } else if (grid[x][y] == MAX_HEIGHT - 1) {
            grid[x][y] = MAX_HEIGHT; // Build dome
        } else {
            return false; // Already a dome
        }
        return true;
    }

    /**
     * Checks if a move is valid according to adjacency and height rules.
     *
     * @param fromX The X-coordinate of the source position.
     * @param fromY The Y-coordinate of the source position.
     * @param toX   The X-coordinate of the target position.
     * @param toY   The Y-coordinate of the target position.
     * @return True if the move is valid; false otherwise.
     */
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        if (!isWithinBounds(fromX, fromY) || !isWithinBounds(toX, toY)) {
            return false;
        }

        if (Math.abs(fromX - toX) > 1 || Math.abs(fromY - toY) > 1) {
            return false; // Must move to an adjacent position
        }

        if (isOccupied(toX, toY)) {
            return false; // Target position must be unoccupied
        }

        if (grid[toX][toY] - grid[fromX][fromY] > 1) {
            return false; // Can climb at most one level
        }

        return true;
    }

    /**
     * Places a worker at the specified position.
     *
     * @param x      The X-coordinate (0-based).
     * @param y      The Y-coordinate (0-based).
     * @param worker The worker to place.
     * @return True if the placement was successful; false otherwise.
     */
    public boolean placeWorker(int x, int y, Worker worker) {
        if (worker == null || !isWithinBounds(x, y) || isOccupied(x, y)) {
            return false; // Invalid placement
        }

        workers[x][y] = worker;
        worker.setPosition(x, y); // Update worker's position
        return true;
    }

    /**
     * Moves a worker from one position to another.
     *
     * @param fromX The X-coordinate of the current position.
     * @param fromY The Y-coordinate of the current position.
     * @param toX   The X-coordinate of the target position.
     * @param toY   The Y-coordinate of the target position.
     * @return True if the move was successful; false otherwise.
     */
    public boolean moveWorker(int fromX, int fromY, int toX, int toY) {
        if (!isValidMove(fromX, fromY, toX, toY)) {
            return false; // Invalid move
        }

        Worker worker = workers[fromX][fromY];
        if (worker == null) {
            return false; // No worker to move
        }

        workers[toX][toY] = worker;
        workers[fromX][fromY] = null;
        worker.setPosition(toX, toY); // Update worker's position
        return true;
    }

    /**
     * Retrieves the worker at the specified position.
     *
     * @param x The X-coordinate (0-based).
     * @param y The Y-coordinate (0-based).
     * @return The worker at the position, or null if unoccupied.
     */
    public Worker getWorkerAt(int x, int y) {
        return isWithinBounds(x, y) ? workers[x][y] : null;
    }

    /**
     * Resets the board to its initial state.
     */
    public void resetBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(grid[i], 0);
            Arrays.fill(workers[i], null);
        }
    }

    /**
     * Retrieves a copy of the grid representing tower heights.
     *
     * @return A deep copy of the grid.
     */
    public int[][] getGrid() {
        int[][] copy = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, BOARD_SIZE);
        }
        return copy;
    }

    /**
     * Checks if the specified coordinates are within board boundaries.
     *
     * @param x The X-coordinate.
     * @param y The Y-coordinate.
     * @return True if within bounds; false otherwise.
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }


    public boolean forceMoveWorker(int fromX, int fromY, int toX, int toY) {
        if (!isWithinBounds(fromX, fromY) || !isWithinBounds(toX, toY)) {
            return false;
        }

        Worker worker = workers[fromX][fromY];
        if (worker == null) {
            return false; // No worker to move
        }

        if (workers[toX][toY] != null) {
            return false; // Target position must be unoccupied
        }

        workers[toX][toY] = worker;
        workers[fromX][fromY] = null;
        worker.setPosition(toX, toY);
        return true;
    }

    /**
     * Provides a string representation of the board for debugging.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Board State:\n");
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                sb.append(String.format("[%d%s] ", grid[i][j],
                        workers[i][j] != null ? "W" : " "));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Checks if two Board objects are equal based on their grid and workers.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Board)) return false;
        Board other = (Board) obj;
        return Arrays.deepEquals(this.grid, other.grid) &&
                Arrays.deepEquals(this.workers, other.workers);
    }

    /**
     * Generates a hash code for the board.
     */
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(grid), Arrays.deepHashCode(workers));
    }
}
