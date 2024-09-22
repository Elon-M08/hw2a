package org.example;

public class Worker {
    private int x;
    private int y;
    private Player owner;

    public Worker(Player owner, int startX, int startY) {
        this.owner = owner;
        this.x = startX;
        this.y = startY;
    }

    // Get the current position of the worker.
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Player getOwner() {
        return owner;
    }

    // Move the worker to a new position (only if valid).
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

