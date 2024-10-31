package org.example;

/**
 * The Worker class represents a worker piece in the game, associated with a player and 
 * positioned on the game board.
 *
 * Each worker has:
 * - A position on the board (`x` and `y` coordinates) that tracks its current location.
 * - An `owner`, represented by a `Player` object, which indicates the player controlling 
 *   this worker.
 *
 * Key Methods:
 * - `move(int newX, int newY, Board board)`: Attempts to move the worker to a new 
 *   position on the board, verifying the move is valid according to the game rules.
 * - `getX()` and `getY()`: Return the worker's current coordinates.
 * - `getOwner()`: Returns the player who owns the worker.
 *
 * This class encapsulates the worker's movement and state management, allowing 
 * for interaction with the game board and enforcing player ownership of each worker.
 */
public class Worker {
    private int x;
    private int y;
    private Player owner;

    /**
     * Initializes a new worker with a specified owner and starting position.
     *
     * This constructor sets the worker's owner (player) and initial coordinates on the board.
     *
     * @param owner The player to whom this worker belongs.
     * @param startX The starting x-coordinate of the worker on the board.
     * @param startY The starting y-coordinate of the worker on the board.
     *
     * @post The worker is initialized with the specified owner and position, ready for gameplay.
     */
    public Worker(Player owner, int startX, int startY) {
        this.owner = owner;
        this.x = startX;
        this.y = startY;
    }

    
    /**
     * Gets the current x-coordinate of the worker.
     *
     * @return The x-coordinate of the worker's current position.
     */
    public int getX() {
        return x;
    }
    /**
     * Gets the current y-coordinate of the worker.
     *
     * @return The y-coordinate of the worker's current position.
     */
    public int getY() {
        return y;
    }
    /**
     * Retrieves the owner of the worker.
     *
     * @return The `Player` object that owns this worker.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Moves the worker to a new position on the board if the move is valid.
     *
     * This method checks if a move to the specified `(newX, newY)` coordinates is valid 
     * according to the board's rules. If valid, the worker’s position is updated both 
     * on the board and within the worker's internal state.
     *
     * @param newX The x-coordinate of the target position for the move.
     * @param newY The y-coordinate of the target position for the move.
     * @param board The game board on which the move is attempted.
     * @return `true` if the move is successful; `false` if the move is invalid.
     *
     * @pre `(newX, newY)` represents a valid position on the board, and `board` is a 
     *      properly initialized game board.
     * @post If the move is valid, the worker's position is updated on the board and 
     *       internally. If the move is invalid, no changes are made.
     */
    public boolean move(int newX, int newY, Board board) {
        // Check if the move is valid using the board's validation logic.
        if (board.isValidMove(x, y, newX, newY)) {
            // Update the worker's position.
            board.moveWorker(x, y, newX, newY);
            this.x = newX;
            this.y = newY;
            return true;
        } else {
            return false;
        }
    }
}

