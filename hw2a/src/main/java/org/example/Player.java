package org.example;

public class Player {
    private String name;
    private Worker[] workers;

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

    // Get the player's name.
    public String getName() {
        return name;
    }

    // Get one of the player's workers.
    public Worker getWorker(int index) {
        if (index >= 0 && index < 2) {
            return workers[index];
        }
        return null;
    }

    // Take a turn: Move one of the player's workers and build on an adjacent field.
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

