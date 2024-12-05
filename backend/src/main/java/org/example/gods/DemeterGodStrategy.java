// src/main/java/org/example/gods/DemeterGodStrategy.java 
package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Demeter's Strategy Implementation.
 * Demeter allows building twice on different cells.
 */
public class DemeterGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(DemeterGodStrategy.class.getName());
    
    // Tracks if the player has already built once
    private boolean hasBuiltOnce = false;
    
    // Stores the coordinates of the first build to ensure the second build is on a different cell
    private int firstBuildX = -1;
    private int firstBuildY = -1;

    @Override
    public String getName() {
        return "Demeter";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = super.getStrategyState();
        state.put("hasBuiltOnce", hasBuiltOnce);
        state.put("firstBuildX", firstBuildX);
        state.put("firstBuildY", firstBuildY);
        // Indicate if an extra build is available
        state.put("extraBuildAvailable", hasBuiltOnce);
        return state;
    }

    /**
     * Overrides the build method to allow Demeter to build twice on different cells.
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
        
        if (board.getTowerHeight(x, y) >= 4) {
            logger.warning(getName() + " Strategy: Cannot build beyond height 4.");
            throw new Exception("Cannot build beyond height 4.");
        }
        
        if (hasBuiltOnce) {
            // Ensure the second build is on a different cell
            if (x == firstBuildX && y == firstBuildY) {
                logger.warning(getName() + " Strategy: Second build must be on a different cell.");
                throw new Exception("Second build must be on a different cell.");
            }
        }
        
        // Perform the build using the superclass's build method
        boolean buildSuccess = super.build(game, worker, x, y);
        if (!buildSuccess) {
            logger.warning(getName() + " Strategy: Build failed.");
            return false;
        }
        
        if (!hasBuiltOnce) {
            // Mark that the first build has been completed
            hasBuiltOnce = true;
            firstBuildX = x;
            firstBuildY = y;
            strategyState.put("hasBuiltOnce", true);
            strategyState.put("firstBuildX", firstBuildX);
            strategyState.put("firstBuildY", firstBuildY);
            strategyState.put("extraBuildAvailable", true);
            logger.info(getName() + " Strategy: First build completed at (" + x + ", " + y + "), second build available.");
        } else {
            // Second build completed, reset build state
            hasBuiltOnce = false;
            firstBuildX = -1;
            firstBuildY = -1;
            strategyState.put("hasBuiltOnce", false);
            strategyState.put("firstBuildX", firstBuildX);
            strategyState.put("firstBuildY", firstBuildY);
            strategyState.put("extraBuildAvailable", false);
            logger.info(getName() + " Strategy: Second build completed at (" + x + ", " + y + ").");
        }
        
        return true;
    }

    /**
     * Overrides the nextPhase method to handle phase transitions based on build state.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");
        
        if (hasBuiltOnce) {
            // Awaiting second build, keep the phase as BUILD
            game.setCurrentPhase(Game.GamePhase.BUILD);
            logger.info(getName() + " Strategy: Awaiting second build. Phase remains BUILD.");
        } else {
            // All builds completed, proceed to move phase
            super.nextPhase(game);
            logger.info(getName() + " Strategy: Proceeding to move phase.");
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Demeter's build state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Demeter's build state
        hasBuiltOnce = false;
        firstBuildX = -1;
        firstBuildY = -1;
        strategyState.put("hasBuiltOnce", false);
        strategyState.put("firstBuildX", firstBuildX);
        strategyState.put("firstBuildY", firstBuildY);
        strategyState.put("extraBuildAvailable", false);
        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    /**
     * Demeter's strategy does not utilize the setCannotMoveUp method.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Demeter's strategy does not utilize this method
        // Do nothing
    }
}
