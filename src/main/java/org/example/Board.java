package org.example;

/**
 * The Board class represents a 5x5 game board used in the game Santorini, containing
 * functionality for managing tower heights, worker positions, and movements. Each 
 * field on the board is tracked for its tower height and whether it is occupied by a 
 * worker or dome. This class provides methods to build towers, check for valid moves, 
 * place workers, and move them according to the rules of the game.
 *
 * Fields:
 * - `grid` (int[][]): A 5x5 grid representing the tower height at each position. 
 *   Heights range from 0 (empty) to 3 (maximum tower height), with 4 indicating a dome.
 * - `workers` (Worker[][]): A 5x5 grid representing the presence of workers. Each 
 *   position is either null (no worker) or contains a Worker object.
 *
 * Key Methods:
 * - `isOccupied(int x, int y)`: Checks if a field is occupied by a worker or a dome.
 * - `getTowerHeight(int x, int y)`: Returns the current height of the tower at a given field.
 * - `build(int x, int y)`: Builds a block or dome on an unoccupied field, up to a max 
 *   height of 3, then adds a dome.
 * - `isValidMove(int fromX, int fromY, int toX, int toY)`: Checks if a move from one 
 *   field to an adjacent field is valid according to adjacency and height rules.
 * - `placeWorker(int x, int y, Worker worker)`: Places a worker on the board if the field 
 *   is unoccupied.
 * - `moveWorker(int fromX, int fromY, int toX, int toY)`: Moves a worker from one position 
 *   to another if the move is valid.
 *
 * This class ensures game rules are enforced for tower building, worker placement, and 
 * movement, enabling core gameplay mechanics of the Santorini board.
 */

public class Board {
    private int[][] grid;
    Worker[][] workers;


    /**
     * Initializes a new 5x5 game board with empty towers and no workers.
     *
     * The constructor sets up the board by:
     * - Initializing the `grid` array to represent tower heights, where each field 
     *   starts at 0 (indicating an empty field with no blocks).
     * - Initializing the `workers` array to track worker positions, with all fields 
     *   set to `null` to indicate no workers are initially placed on the board.
     *
     * @post The board is prepared for gameplay with all fields empty and unoccupied.
     */
    public Board() {
        // Initialize the grid to all zeros (0 = empty).
        grid = new int[5][5];
        // Initialize worker positions as null (no worker initially).
        workers = new Worker[5][5];
    }

    /**
     * Checks if a specified field is occupied by either a worker or a dome.
     *
     * This method verifies if a field at the position `(x, y)` is occupied by checking:
     * - If a worker is present at the field (`workers[x][y]` is not `null`).
     * - If the field contains a dome, indicated by a height of 4 in `grid[x][y]`.
     *
     * @param x The x-coordinate of the field.
     * @param y The y-coordinate of the field.
     * @return `true` if the field is occupied by a worker or dome; `false` otherwise.
     *
     * @pre `x` and `y` represent valid coordinates within the bounds of the board.
     */
    public boolean isOccupied(int x, int y) {
        return workers[x][y] != null || grid[x][y] == 4;  // 4 means a dome
    }

    /**
     * Retrieves the current height of the tower at a specific field.
     *
     * This method returns the height of the tower at position `(x, y)` on the board. 
     * Heights range from 0 (no tower) to 4 (a dome on top of a level-3 tower).
     *
     * @param x The x-coordinate of the field.
     * @param y The y-coordinate of the field.
     * @return The height of the tower at `(x, y)`, where:
     *         - 0–3 indicate the tower levels.
     *         - 4 indicates a dome.
     *
     * @pre `x` and `y` are valid coordinates within the bounds of the board.
     */
    public int getTowerHeight(int x, int y) {
        return grid[x][y];
    }


    /**
     * Builds a block or a dome at the specified field, following game rules.
     *
     * This method attempts to increase the height of a tower at the position `(x, y)`. 
     * If the position is unoccupied, the height is incremented:
     * - Levels 0–2 allow blocks to be built, incrementing the height by 1.
     * - At level 3, a dome is built, setting the height to 4 and making the field 
     *   permanently occupied.
     *
     * @param x The x-coordinate of the field where the build action is attempted.
     * @param y The y-coordinate of the field where the build action is attempted.
     * @return `true` if the build action is successful; `false` if the field is occupied.
     *
     * @pre `x` and `y` represent a valid position within the bounds of the board.
     * @post Increments the height of the tower at `(x, y)` or sets it to 4 if 
     *       it reaches level 3, marking it as occupied with a dome.
     */

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

    /**
     * Checks if a move from one field to an adjacent field is valid according to game rules.
     *
     * A move is considered valid if:
     * - The destination (`toX`, `toY`) is within the bounds of the 5x5 grid.
     * - The destination field is adjacent (horizontally, vertically, or diagonally) to the 
     *   starting field (`fromX`, `fromY`).
     * - The destination field is unoccupied (no worker or dome present).
     * - The height difference between the destination and starting fields is at most one 
     *   level, allowing the worker to climb no more than one level higher.
     *
     * @param fromX The x-coordinate of the worker's current position.
     * @param fromY The y-coordinate of the worker's current position.
     * @param toX The x-coordinate of the target position.
     * @param toY The y-coordinate of the target position.
     * @return `true` if the move is valid according to the game rules; `false` otherwise.
     *
     * @pre `fromX` and `fromY` represent a valid position on the board with an existing worker.
     * @post Returns `true` only if all conditions for a valid move are met.
     */
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

    /**
     * Places a worker at the specified position on the board if the field is unoccupied.
     *
     * This method assigns a `Worker` object to the position `(x, y)` on the board 
     * if the field is currently unoccupied. It does not override an existing worker 
     * or dome at that location.
     *
     * @param x The x-coordinate of the field where the worker is to be placed.
     * @param y The y-coordinate of the field where the worker is to be placed.
     * @param worker The `Worker` object to place on the board.
     *
     * @pre `x` and `y` represent valid coordinates within the board's bounds, and 
     *      `worker` is a valid `Worker` object.
     * @post The specified worker is placed at `(x, y)` if the field is unoccupied; 
     *       otherwise, the method does nothing.
     */
    public void placeWorker(int x, int y, Worker worker) {
        if (!isOccupied(x, y)) {
            workers[x][y] = worker;
        }
    }
    

    /**
     * Moves a worker from one position to another on the board if the move is valid.
     *
     * This method transfers a worker from the position `(fromX, fromY)` to the position 
     * `(toX, toY)` if the move meets the conditions defined in `isValidMove`. After the 
     * move, the original position is cleared.
     *
     * @param fromX The x-coordinate of the worker's current position.
     * @param fromY The y-coordinate of the worker's current position.
     * @param toX The x-coordinate of the target position.
     * @param toY The y-coordinate of the target position.
     *
     * @pre `(fromX, fromY)` holds a worker, `(toX, toY)` is a valid, unoccupied adjacent field,
     *      and `isValidMove` confirms that the move abides by game rules.
     * @post If the move is valid, the worker is moved to `(toX, toY)` and removed from 
     *       `(fromX, fromY)`. If invalid, no changes are made.
     */
    public void moveWorker(int fromX, int fromY, int toX, int toY) {
        if (isValidMove(fromX, fromY, toX, toY)) {
            workers[toX][toY] = workers[fromX][fromY];
            workers[fromX][fromY] = null;
        }
    }
}

