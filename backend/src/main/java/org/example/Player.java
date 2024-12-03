package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Player class represents a player in the Santorini game, managing their workers
 * and interactions with the board. Each player has a name and can have up to two workers.
 */
public class Player {
    private final String name;
    private final List<Worker> workers;

    /**
     * Constructor to initialize a player with a name.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.workers = new ArrayList<>();
    }

    /**
     * Retrieves the player's name.
     *
     * @return The name of the player as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a worker to the player's list of workers.
     *
     * @param worker The Worker to add.
     * @throws IllegalStateException If the player already has two workers.
     */
    public void addWorker(Worker worker) {
        if (workers.size() >= 2) {
            throw new IllegalStateException("Player already has two workers.");
        }
        workers.add(worker);
    }

    /**
     * Retrieves the worker at the specified index.
     *
     * @param index The index of the worker (0 or 1).
     * @return The Worker instance if index is valid; null otherwise.
     */
    public Worker getWorker(int index) {
        if (index >= 0 && index < workers.size()) {
            return workers.get(index);
        }
        return null;
    }

    /**
     * Retrieves an unmodifiable list of the player's workers.
     *
     * @return A List containing the player's Worker instances.
     */
    public List<Worker> getWorkers() {
        return Collections.unmodifiableList(workers);
    }
}
