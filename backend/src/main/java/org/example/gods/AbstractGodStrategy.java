// src/main/java/org/example/gods/AbstractGodStrategy.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Abstract base class for God strategies.
 * Provides default implementations to avoid code duplication.
 */
public abstract class AbstractGodStrategy implements GodStrategy {
    protected static final Logger logger = Logger.getLogger(AbstractGodStrategy.class.getName());
    protected Map<String, Object> strategyState = new HashMap<>();

    @Override
    public Map<String, Object> getStrategyState() {
        return strategyState;
    }

    @Override
    public boolean checkVictory(Game game, Worker worker) throws Exception {
        logger.info(getName() + " Strategy: checkVictory called");
        return game.defaultCheckVictory(worker);
    }

    @Override
    public List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception {
        logger.info(getName() + " Strategy: getSelectableMoveCells called");
        Board board = game.getBoard();
        int x = worker.getX();
        int y = worker.getY();
        int currentHeight = board.getTowerHeight(x, y);
        List<Map<String, Integer>> selectableCells = new ArrayList<>();

        // Iterate through all adjacent cells
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip the current cell
                int newX = x + dx;
                int newY = y + dy;

                if (!board.isWithinBounds(newX, newY)) continue;

                int targetHeight = board.getTowerHeight(newX, newY);
                Worker targetWorker = board.getWorkerAt(newX, newY);

                // Check height difference
                if (targetHeight - currentHeight > 1) continue;

                if (targetWorker == null) {
                    // Standard selectable cell
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                }
                // Else, occupied by another worker; specific strategies can handle accordingly
            }
        }

        logger.info(getName() + " Strategy: Selectable move cells determined");
        return selectableCells;
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        logger.info(getName() + " Strategy: getSelectableBuildCells called");
        Board board = game.getBoard();
        int x = worker.getX();
        int y = worker.getY();
        List<Map<String, Integer>> selectableBuildCells = new ArrayList<>();

        // Iterate through all adjacent cells for building
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // Skip the current cell
                int buildX = x + dx;
                int buildY = y + dy;

                if (!board.isWithinBounds(buildX, buildY)) continue;
                if (board.isOccupied(buildX, buildY)) continue;
                if (board.getTowerHeight(buildX, buildY) >= 4) continue; // Max height reached

                Map<String, Integer> cell = new HashMap<>();
                cell.put("x", buildX);
                cell.put("y", buildY);
                selectableBuildCells.add(cell);
            }
        }

        logger.info(getName() + " Strategy: Selectable build cells determined");
        return selectableBuildCells;
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called");
        boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
        logger.info(getName() + " Strategy: move success - " + moveSuccess);
        return moveSuccess;
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: build called");
        boolean buildSuccess = game.defaultBuild(worker, x, y);
        logger.info(getName() + " Strategy: build success - " + buildSuccess);
        return buildSuccess;
    }

    
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");
        // Default behavior: transition to BUILD after MOVE, then to MOVE after BUILD
        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
            logger.info(getName() + " Strategy: Transitioned to BUILD phase.");
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            game.switchPlayer();
            game.setCurrentPhase(Game.GamePhase.MOVE);
            logger.info(getName() + " Strategy: Transitioned to MOVE phase for " + game.getCurrentPlayer().getName());
        } else {
            logger.severe(getName() + " Strategy: nextPhase called in unexpected phase: " + game.getCurrentPhase());
            throw new Exception("nextPhase called in unexpected phase: " + game.getCurrentPhase());
        }
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        // Reset any strategy-specific states
        // For Default strategy, nothing to reset
        game.switchPlayer();
        game.setCurrentPhase(Game.GamePhase.MOVE);
        logger.info("Default Strategy: Ended turn and switched player.");
    }

    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Default implementation does nothing
    }
}
