// src/main/java/org/example/gods/HephaestusGodStrategy.java
package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hephaestus's Strategy Implementation.
 * Hephaestus allows building twice on the same cell, but the second build cannot be a dome.
 */
public class HephaestusGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(HephaestusGodStrategy.class.getName());
    
    // Tracks if the player has performed an extra build on the same cell
    private boolean hasPerformedExtraBuild = false;
    
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
        state.put("hasPerformedExtraBuild", hasPerformedExtraBuild);
        state.put("firstBuildX", firstBuildX);
        state.put("firstBuildY", firstBuildY);
        // Indicate if an extra build is available
        state.put("extraBuildAvailable", hasPerformedExtraBuild);
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

        if (hasPerformedExtraBuild) {
            logger.warning(getName() + " Strategy: Extra build has already been performed.");
            throw new Exception("Extra build has already been performed.");
        }

        if (firstBuildX == -1 && firstBuildY == -1) {
            // First build
            boolean buildSuccess = super.build(game, worker, x, y);
            if (buildSuccess) {
                hasPerformedExtraBuild = true;
                firstBuildX = x;
                firstBuildY = y;
                strategyState.put("hasPerformedExtraBuild", true);
                strategyState.put("firstBuildX", firstBuildX);
                strategyState.put("firstBuildY", firstBuildY);
                strategyState.put("extraBuildAvailable", true);
                logger.info(getName() + " Strategy: First build completed at (" + x + ", " + y + "), extra build available on the same cell.");
            }
            return buildSuccess;
        } else {
            // Extra build on the same cell
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
                hasPerformedExtraBuild = false;
                firstBuildX = -1;
                firstBuildY = -1;
                strategyState.put("hasPerformedExtraBuild", false);
                strategyState.put("firstBuildX", firstBuildX);
                strategyState.put("firstBuildY", firstBuildY);
                strategyState.put("extraBuildAvailable", false);
                logger.info(getName() + " Strategy: Extra build completed at (" + x + ", " + y + ").");
            }
            return buildSuccess;
        }
    }

    /**
     * Overrides the nextPhase method to handle phase transitions based on build state.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");

        if (hasPerformedExtraBuild && firstBuildX != -1 && firstBuildY != -1) {
            // Awaiting extra build on the same cell
            game.setCurrentPhase(Game.GamePhase.BUILD);
            logger.info(getName() + " Strategy: Awaiting extra build on (" + firstBuildX + ", " + firstBuildY + "). Phase remains BUILD.");
        } else {
            // All builds completed, proceed to move phase
            super.nextPhase(game);
            logger.info(getName() + " Strategy: Proceeding to move phase.");
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Hephaestus's build state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Hephaestus's build state
        hasPerformedExtraBuild = false;
        firstBuildX = -1;
        firstBuildY = -1;
        strategyState.put("hasPerformedExtraBuild", false);
        strategyState.put("firstBuildX", firstBuildX);
        strategyState.put("firstBuildY", firstBuildY);
        strategyState.put("extraBuildAvailable", false);
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
