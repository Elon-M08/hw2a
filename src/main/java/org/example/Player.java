package org.example;

public class Player {
    private String name;
    private Worker[] workers;

    /**
     * Initializes a new player with a name and two workers at specified starting positions.
     *
     * This constructor sets up the player's attributes and places the player's workers on 
     * the board. Each worker is assigned its starting coordinates, and then both workers 
     * are placed on the `board`.
     *
     * @param name The name of the player.
     * @param worker1StartX The x-coordinate for the starting position of the first worker.
     * @param worker1StartY The y-coordinate for the starting position of the first worker.
     * @param worker2StartX The x-coordinate for the starting position of the second worker.
     * @param worker2StartY The y-coordinate for the starting position of the second worker.
     * @param board The game board where the workers are placed.
     *
     * @post The player's name is set, two workers are created with the specified starting 
     *       positions, and both are placed on the board at their respective positions.
     */
    public Player(String name, int worker1StartX, int worker1StartY, int worker2StartX, int worker2StartY, Board board) {
        this.name = name;
        workers = new Worker[2];
        // Initialize the two workers at the specified starting positions.
        workers[0] = new Worker(this, worker1StartX, worker1StartY);
        workers[1] = new Worker(this, worker2StartX, worker2StartY);
        // Place the workers on the board.
        board.placeWorker(worker1StartX, worker1StartY, workers[0]);
        board.placeWorker(worker2StartX, worker2StartY, workers[1]);
    }

    /**
     * Retrieves the player's name.
     *
     * @return The name of the player as a `String`.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves a worker by its index.
     *
     * This method returns one of the player's workers based on the given index, provided 
     * the index is valid (0 or 1).
     *
     * @param index The index of the worker to retrieve (expected to be 0 or 1).
     * @return The `Worker` object at the specified index if valid; `null` otherwise.
     *
     * @pre `index` should be 0 or 1 to access a valid worker.
     * @post Returns the corresponding `Worker` if `index` is valid; otherwise, returns `null`.
     */
    public Worker getWorker(int index) {
        if (index >= 0 && index < 2) {
            return workers[index];
        }
        return null;
    }

    /**
     * Executes a player's turn by moving a worker and building on an adjacent field.
     *
     * This method performs the following actions in sequence:
     * - Retrieves the specified worker (`workerIndex`) and attempts to move it to the 
     *   target position (`moveX`, `moveY`) on the given `board`.
     * - If the move is successful, it then attempts to build on the specified build 
     *   position (`buildX`, `buildY`).
     *
     * @param workerIndex The index of the worker to be moved (typically 0 or 1).
     * @param moveX The x-coordinate of the target position for the move.
     * @param moveY The y-coordinate of the target position for the move.
     * @param buildX The x-coordinate of the position where the build is attempted.
     * @param buildY The y-coordinate of the position where the build is attempted.
     * @param board The game board where the move and build actions take place.
     * @return `true` if both the move and build actions are successful; `false` otherwise.
     *
     * @pre `workerIndex` is valid for the current player's workers, and `board` is a 
     *      properly initialized game board.
     * @post If the move is valid, the worker is relocated, and a build is attempted.
     *       If either action fails, the method returns `false`, and no changes are made.
     */
    public boolean takeTurn(int workerIndex, int moveX, int moveY, int buildX, int buildY, Board board) {
        Worker selectedWorker = getWorker(workerIndex);

        if (selectedWorker != null) {
            // Move the worker.
            boolean moveSuccess = selectedWorker.move(moveX, moveY, board);

            // If the move is successful, build on the adjacent field.
            if (moveSuccess) {
                return board.build(buildX, buildY);
            }
        }

        return false;  // Return false if the move or build fails.
    }
}

