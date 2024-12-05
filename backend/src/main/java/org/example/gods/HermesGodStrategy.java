// src/main/java/org/example/gods/HermesGodStrategy.java
package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hermes's Strategy Implementation.
 * Hermes allows a player to move their worker twice per turn, but cannot move back to the original space.
 */
public class HermesGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(HermesGodStrategy.class.getName());

    // Tracks the number of moves performed in the current turn
    private int moveCount = 0;

    // Stores the original position to prevent moving back
    private int originalX = -1;
    private int originalY = -1;

    @Override
    public String getName() {
        return "Hermes";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = super.getStrategyState();
        state.put("moveCount", moveCount);
        state.put("originalX", originalX);
        state.put("originalY", originalY);
        return state;
    }

    /**
     * Overrides the move method to allow Hermes to move twice per turn,
     * but cannot move back to the original space.
     */
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called to (" + x + ", " + y + ")");

        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();
        int currentHeight = board.getTowerHeight(fromX, fromY);
        int targetHeight = board.getTowerHeight(x, y);

        // Validation: Height difference constraints
        if (targetHeight - currentHeight > 1 || targetHeight < currentHeight - 1) {
            logger.warning(getName() + " Strategy: Invalid move height difference.");
            throw new Exception("Cannot move up or down more than one level with Hermes's power.");
        }

        // If it's the first move of the turn, store the original position
        if (moveCount == 0) {
            originalX = fromX;
            originalY = fromY;
            strategyState.put("originalX", originalX);
            strategyState.put("originalY", originalY);
            logger.info(getName() + " Strategy: Original position set to (" + originalX + ", " + originalY + ")");
        }

        // Prevent moving back to the original position
        if (x == originalX && y == originalY) {
            logger.warning(getName() + " Strategy: Cannot move back to the original position.");
            throw new Exception("Cannot move back to the original position with Hermes's power.");
        }

        // Perform the move using the superclass's move method
        boolean moveSuccess = super.move(game, worker, x, y);
        if (!moveSuccess) {
            logger.warning(getName() + " Strategy: Move failed.");
            throw new Exception("Invalid move. Try again.");
        }

        moveCount++;
        strategyState.put("moveCount", moveCount);
        logger.info(getName() + " Strategy: Move count updated to " + moveCount);

        return true;
    }

    /**
     * Overrides the nextPhase method to handle phase transitions based on move count.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");

        if (moveCount < 2) {
            // Awaiting second move
            logger.info(getName() + " Strategy: Awaiting second move.");
            // Remain in MOVE phase
            game.setCurrentPhase(Game.GamePhase.MOVE);
        } else {
            // Both moves completed, proceed to build phase
            super.nextPhase(game);
            // Reset move count and original position for the next turn
            moveCount = 0;
            originalX = -1;
            originalY = -1;
            strategyState.put("moveCount", moveCount);
            strategyState.put("originalX", originalX);
            strategyState.put("originalY", originalY);
            logger.info(getName() + " Strategy: Both moves completed. Proceeding to build phase.");
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Hermes's state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Hermes's move state
        moveCount = 0;
        originalX = -1;
        originalY = -1;
        strategyState.put("moveCount", moveCount);
        strategyState.put("originalX", originalX);
        strategyState.put("originalY", originalY);
        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    /**
     * Hermes's strategy does not utilize the setCannotMoveUp method.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Hermes's strategy does not utilize this method
        // Do nothing
    }
}
