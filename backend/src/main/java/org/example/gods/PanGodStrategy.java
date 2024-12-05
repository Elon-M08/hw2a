// src/main/java/org/example/gods/PanGodStrategy.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Pan's Strategy Implementation.
 * Pan allows a player to win by moving down two or more levels in a single move, in addition to the standard victory condition of reaching level 3.
 */
public class PanGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(PanGodStrategy.class.getName());

    // Tracks the last move's height difference
    private int lastMoveHeightDifference = 0;

    @Override
    public String getName() {
        return "Pan";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = super.getStrategyState();
        state.put("lastMoveHeightDifference", lastMoveHeightDifference);
        return state;
    }

    /**
     * Overrides the move method to track height differences for Pan's victory condition.
     */
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called from (" + worker.getX() + ", " + worker.getY() + ") to (" + x + ", " + y + ")");

        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();

        // Get current height before move
        int currentHeight = board.getTowerHeight(fromX, fromY);

        // Perform the move using the superclass's move method
        boolean moveSuccess = super.move(game, worker, x, y);
        if (!moveSuccess) {
            logger.warning(getName() + " Strategy: Move failed.");
            throw new Exception("Invalid move. Try again.");
        }

        // Get new height after move
        int newHeight = board.getTowerHeight(x, y);

        // Calculate height difference
        lastMoveHeightDifference = newHeight - currentHeight;
        strategyState.put("lastMoveHeightDifference", lastMoveHeightDifference);

        logger.info(getName() + " Strategy: Move completed with height difference of " + lastMoveHeightDifference);

        return true;
    }

    /**
     * Overrides the checkVictory method to include Pan's special victory condition.
     */
    @Override
    public boolean checkVictory(Game game, Worker worker) throws Exception {
        Board board = game.getBoard();
        int currentHeight = board.getTowerHeight(worker.getX(), worker.getY());

        // Standard victory condition: Reached level 3
        boolean standardVictory = currentHeight == 3;

        // Pan's special victory condition: Moved down two or more levels
        boolean panVictory = lastMoveHeightDifference <= -2;

        if (standardVictory || panVictory) {
            logger.info(getName() + " Strategy: Victory condition met. standardVictory: " + standardVictory + ", panVictory: " + panVictory);
            return true;
        }

        logger.info(getName() + " Strategy: Victory condition not met. standardVictory: " + standardVictory + ", panVictory: " + panVictory);
        return false;
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Pan's last move height difference
        lastMoveHeightDifference = 0;
        strategyState.put("lastMoveHeightDifference", lastMoveHeightDifference);
        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Pan's strategy does not utilize this method
        // Do nothing
    }
}
