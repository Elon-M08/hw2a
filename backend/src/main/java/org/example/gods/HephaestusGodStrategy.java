// src/main/java/org/example/gods/HephaestusGodStrategy.java
package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;

import java.util.ArrayList; // Added import
import java.util.HashMap;
import java.util.List; // Added import
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hephaestus's Strategy Implementation.
 * Hephaestus allows building twice on the same cell, but the second build cannot be a dome.
 */
public class HephaestusGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(HephaestusGodStrategy.class.getName());
    
    // Tracks if an extra build is available
    private boolean extraBuildAvailable = false;
    
    // Stores the coordinates of the first build to ensure the extra build is on the same cell
    private int firstBuildX = -1;
    private int firstBuildY = -1;

    @Override
    public String getName() {
        return "Hephaestus";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = super.getStrategyState();
        state.put("extraBuildAvailable", extraBuildAvailable);
        state.put("firstBuildX", firstBuildX);
        state.put("firstBuildY", firstBuildY);
        return state;
    }

    /**
     * Overrides the build method to allow Hephaestus to build twice on the same cell,
     * ensuring the second build does not result in a dome.
     */
    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: build called at (" + x + ", " + y + ")");

        Board board = game.getBoard();

        // Validate build position
        if (!board.isAdjacent(worker.getX(), worker.getY(), x, y)) {
            logger.warning(getName() + " Strategy: Build position must be adjacent.");
            throw new Exception("Build position must be adjacent.");
        }

        if (board.isOccupied(x, y)) {
            logger.warning(getName() + " Strategy: Cannot build on an occupied space.");
            throw new Exception("Cannot build on an occupied space.");
        }

        int currentHeight = board.getTowerHeight(x, y);
        if (currentHeight >= 4) {
            logger.warning(getName() + " Strategy: Cannot build beyond height 4.");
            throw new Exception("Cannot build beyond height 4.");
        }

        if (extraBuildAvailable) {
            // Extra build is available; ensure it's on the same cell
            if (x != firstBuildX || y != firstBuildY) {
                logger.warning(getName() + " Strategy: Extra build must be on the same cell as the first build.");
                throw new Exception("Extra build must be on the same cell as the first build.");
            }

            // Ensure the extra build does not result in a dome
            if (currentHeight >= 3) { // Building on height 3 would make it 4 (dome), which is not allowed
                logger.warning(getName() + " Strategy: Cannot perform extra build that results in a dome.");
                throw new Exception("Cannot perform extra build that results in a dome.");
            }

            // Perform the extra build
            boolean buildSuccess = super.build(game, worker, x, y);
            if (buildSuccess) {
                extraBuildAvailable = false;
                firstBuildX = -1;
                firstBuildY = -1;
                strategyState.put("extraBuildAvailable", false);
                strategyState.put("firstBuildX", firstBuildX);
                strategyState.put("firstBuildY", firstBuildY);
                logger.info(getName() + " Strategy: Extra build completed at (" + x + ", " + y + ").");
            }
            return buildSuccess;
        } else {
            // First build
            boolean buildSuccess = super.build(game, worker, x, y);
            if (buildSuccess) {
                extraBuildAvailable = true;
                firstBuildX = x;
                firstBuildY = y;
                strategyState.put("extraBuildAvailable", true);
                strategyState.put("firstBuildX", firstBuildX);
                strategyState.put("firstBuildY", firstBuildY);
                logger.info(getName() + " Strategy: First build completed at (" + x + ", " + y + "), extra build available on the same cell.");
            }
            return buildSuccess;
        }
    }

    /**
     * Overrides the getSelectableBuildCells method to provide appropriate build options.
     * When an extra build is available, only the first build cell is selectable.
     */
    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        List<Map<String, Integer>> selectableBuildCells = new ArrayList<>();

        if (extraBuildAvailable && firstBuildX != -1 && firstBuildY != -1) {
            // Only allow building on the first build cell
            Map<String, Integer> cell = new HashMap<>();
            cell.put("x", firstBuildX);
            cell.put("y", firstBuildY);
            selectableBuildCells.add(cell);
            logger.info(getName() + " Strategy: Selectable build cell limited to (" + firstBuildX + ", " + firstBuildY + ") for extra build.");
        } else {
            // Delegate to the default strategy to get selectable build cells
            selectableBuildCells = super.getSelectableBuildCells(game, worker);
            logger.info(getName() + " Strategy: Selectable build cells determined by default strategy.");
        }

        return selectableBuildCells;
    }

    /**
     * Overrides the nextPhase method to handle phase transitions based on build state.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");

        if (extraBuildAvailable) {
            // Awaiting extra build on the same cell
            // Phase remains BUILD to allow the second build
            game.setCurrentPhase(Game.GamePhase.BUILD);
            logger.info(getName() + " Strategy: Awaiting extra build on (" + firstBuildX + ", " + firstBuildY + "). Phase remains BUILD.");
        } else {
            // All builds completed, proceed to the next phase
            super.nextPhase(game);
            logger.info(getName() + " Strategy: Proceeding to next phase.");
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Hephaestus's build state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Hephaestus's build state
        extraBuildAvailable = false;
        firstBuildX = -1;
        firstBuildY = -1;
        strategyState.put("extraBuildAvailable", false);
        strategyState.put("firstBuildX", firstBuildX);
        strategyState.put("firstBuildY", firstBuildY);
        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    /**
     * Hephaestus's strategy does not utilize the setCannotMoveUp method.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Hephaestus's strategy does not utilize this method
        // Do nothing
    }
}
