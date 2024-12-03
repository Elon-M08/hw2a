package org.example;

import java.util.Objects;

/**
 * Represents a worker in the Santorini game, maintaining its current position and ownership.
 * A worker is owned by a player and resides on the game board at specific coordinates.
 *
 * <p>Workers can be moved according to the game's rules, and their positions are synchronized
 * with the game board to ensure consistent game state.</p>
 */
public class Worker {
    private static int idCounter = 0; // Static counter for unique IDs
    private int id;
    private Player owner;
    private int x;
    private int y;

    public Worker(Player owner, int x, int y) {
        this.id = idCounter++;
        this.owner = owner;
        setPosition(x, y);
    }

    public int getId() {
        return id;
    }

    /**
     * Retrieves the current x-coordinate of the worker.
     *
     * @return The current x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the current y-coordinate of the worker.
     *
     * @return The current y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the owner of the worker.
     *
     * @return The Player who owns this worker.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Updates the worker's position on the board.
     * This method should only be called by the Board class to maintain consistency.
     *
     * @param newX The new X-coordinate.
     * @param newY The new Y-coordinate.
     */
    void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Retrieves the worker's current position as a Position object.
     *
     * @return The Position of the worker.
     */
    public Position getPosition() {
        return new Position(x, y);
    }

    /**
     * Provides a string representation of the worker.
     *
     * @return A string detailing the worker's owner and position.
     */
    @Override
    public String toString() {
        return String.format("Worker[Owner=%s, Position=(%d, %d)]", owner.getName(), x, y);
    }
    
    /**
     * Checks if this worker is equal to another object.
     *
     * @param obj The object to compare with.
     * @return True if the objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Worker)) return false;
        Worker other = (Worker) obj;
        return this.x == other.x && this.y == other.y && this.owner.equals(other.owner);
    }

    /**
     * Generates a hash code for the worker.
     *
     * @return The hash code based on the worker's position and owner.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, owner);
    }
}
