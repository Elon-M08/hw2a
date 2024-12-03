package org.example;

import java.util.Objects;

/**
 * The Position class represents a coordinate on the Santorini game board.
 * It encapsulates the X and Y coordinates and provides utility methods for comparison.
 */
public class Position {
    private final int x;
    private final int y;

    /**
     * Constructor to initialize a position with specified coordinates.
     *
     * @param x The X-coordinate (0-based).
     * @param y The Y-coordinate (0-based).
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the X-coordinate.
     *
     * @return The X-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y-coordinate.
     *
     * @return The Y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Provides a string representation of the position.
     *
     * @return A string detailing the X and Y coordinates.
     */
    @Override
    public String toString() {
        return String.format("Position(X=%d, Y=%d)", x, y);
    }

    /**
     * Checks if this position is equal to another object.
     *
     * @param obj The object to compare with.
     * @return True if the positions are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Generates a hash code for the position.
     *
     * @return The hash code based on the coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
