// src/main/java/org/example/gods/GodStrategy.java
package org.example.gods;

import org.example.Game;
import org.example.Worker;

import java.util.List;
import java.util.Map;

/**
 * Interface representing a God Strategy in the game.
 */
public interface GodStrategy {
    String getName()throws Exception;
    Map<String, Object> getStrategyState()throws Exception;
    boolean checkVictory(Game game, Worker worker)throws Exception;
    List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker)throws Exception;
    List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker)throws Exception;
    boolean move(Game game, Worker worker, int x, int y) throws Exception;
    boolean build(Game game, Worker worker, int x, int y) throws Exception;
    void nextPhase(Game game)throws Exception;
    void playerEndsTurn(Game game)throws Exception;
    
    void setCannotMoveUp(boolean cannotMoveUp);
    // void setBuildDome(boolean buildDome);
    
}
