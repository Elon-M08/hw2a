package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.gods.GodStrategy;

public class Player {
    private final String name;
    private final List<Worker> workers;
    private final GodStrategy godStrategy;

    public Player(String name, GodStrategy godStrategy) {
        this.name = name;
        this.godStrategy = godStrategy;
        this.workers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Worker> getWorkers() {
        return Collections.unmodifiableList(workers);
    }

    public GodStrategy getGodStrategy() {
        return godStrategy;
    }

    public void addWorker(Worker worker) {
        if (workers.size() >= 2) {
            throw new IllegalStateException("Player already has two workers.");
        }
        workers.add(worker);
    }

    public Worker getWorker(int index) {
        if (index >= 0 && index < workers.size()) {
            return workers.get(index);
        }
        return null;
    }
}
