package org.example.gods;

import org.example.Game;
import org.example.Worker;
import java.util.List;
import java.util.Map;

public interface GodStrategy {
    String getName();
    boolean move(Game game, Worker worker, int x, int y) throws Exception;
    boolean build(Game game, Worker worker, int x, int y) throws Exception;
    boolean checkVictory(Game game, Worker worker) throws Exception;

    List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception;
    List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception;

    void nextPhase(Game game) throws Exception;
    Map<String, Object> getStrategyState(); // New method
}
