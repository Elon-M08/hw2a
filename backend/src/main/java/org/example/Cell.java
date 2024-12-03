package org.example;

/**
 * The Cell class represents a single cell on the Santorini game board.
 * It maintains the height of the tower built on it, any worker that occupies it,
 * and whether the cell is selectable for an action.
 */
public class Cell {
    private int height;      // The height of the tower on this cell (0-4)
    private Worker worker;   // The worker occupying this cell, if any
    private boolean selectable; // Indicates if the cell can be chosen

    /**
     * Constructor to initialize the cell with a default height of 0, no worker, and not selectable.
     */
    public Cell() {
        this.height = 0;
        this.worker = null;
        this.selectable = false;
    }

    /**
     * Retrieves the current height of the tower on this cell.
     *
     * @return The height of the tower (0-4).
     */
    public int getHeight() {
        return height;
    }

    /**
     * Increases the height of the tower by 1, up to a maximum of 4 (dome).
     *
     * @return True if the height was increased; false if already at maximum height.
     */
    public boolean increaseHeight() {
        if (height < 4) {
            height++;
            return true;
        }
        return false;
    }

    /**
     * Sets the height of the tower on this cell.
     *
     * @param height The new height to set (should be between 0 and 4).
     */
    public void setHeight(int height) {
        if (height >= 0 && height <= 4) {
            this.height = height;
        } else {
            throw new IllegalArgumentException("Height must be between 0 and 4.");
        }
    }

    /**
     * Checks if a worker is present on this cell.
     *
     * @return True if a worker occupies this cell; false otherwise.
     */
    public boolean hasWorker() {
        return worker != null;
    }

    /**
     * Retrieves the worker occupying this cell.
     *
     * @return The Worker instance if present; null otherwise.
     */
    public Worker getWorker() {
        return worker;
    }

    /**
     * Places a worker on this cell.
     *
     * @param worker The Worker to place on this cell.
     * @return True if the worker was placed successfully; false if the cell is already occupied.
     */
    public boolean setWorker(Worker worker) {
        if (this.worker == null) {
            this.worker = worker;
            return true;
        }
        return false;
    }

    /**
     * Removes the worker from this cell.
     *
     * @return True if a worker was removed; false if there was no worker on this cell.
     */
    public boolean removeWorker() {
        if (this.worker != null) {
            this.worker = null;
            return true;
        }
        return false;
    }

    /**
     * Checks if the cell is selectable for an action.
     *
     * @return True if the cell can be chosen; false otherwise.
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Sets whether the cell is selectable.
     *
     * @param selectable True if the cell can be chosen; false otherwise.
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * Provides a string representation of the cell for debugging purposes.
     *
     * @return A string detailing the cell's height, worker status, and selectability.
     */
    @Override
    public String toString() {
        String workerInfo = hasWorker() ? worker.getOwner().getName() : "None";
        return String.format("Cell[Height=%d, Worker=%s, Selectable=%b]", height, workerInfo, selectable);
    }
}