// src/main/java/org/example/gods/AthenaGodStrategy.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Athena's Strategy Implementation.
 * Athena's presence on the board forbids opponents from moving up during their subsequent moves.
 */
public class AthenaGodStrategy extends AbstractGodStrategy {
    private static final Logger logger = Logger.getLogger(AthenaGodStrategy.class.getName());

    private boolean hasMovedUp = false;

    @Override
    public String getName() {
        return "Athena";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = new HashMap<>();
        state.put("hasMovedUp", hasMovedUp);
        return state;
    }

    /**
     * Overrides the move method to implement Athena's special ability.
     * If Athena moves up, opponents cannot move up in their next turn.
     */
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called");

        Board board = game.getBoard();
        int fromHeight = board.getTowerHeight(worker.getX(), worker.getY());
        boolean moveSuccess = super.move(game, worker, x, y);

        if (moveSuccess) {
            int toHeight = board.getTowerHeight(x, y);
            if (toHeight > fromHeight) {
                hasMovedUp = true;
                strategyState.put("hasMovedUp", true);
                logger.info(getName() + " Strategy: Worker moved up");
            } else {
                hasMovedUp = false;
                strategyState.put("hasMovedUp", false);
                logger.info(getName() + " Strategy: Worker did not move up");
            }
        }

        return moveSuccess;
    }

    /**
     * Overrides the nextPhase method to handle phase transitions after moving or building.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called");
        Game.GamePhase currentPhase = game.getCurrentPhase();

        if (currentPhase == Game.GamePhase.MOVE) {
            // After MOVE phase, transition to BUILD phase
            game.setCurrentPhase(Game.GamePhase.BUILD);
            logger.info(getName() + " Strategy: Transitioned to BUILD phase.");
        } else if (currentPhase == Game.GamePhase.BUILD) {
            // After BUILD phase, switch player and transition to MOVE phase
            game.switchPlayer();
            game.setCurrentPhase(Game.GamePhase.MOVE);

            // If Athena has moved up in the previous turn, restrict opponent's movement
            if (hasMovedUp) {
                GodStrategy opponentStrategy = game.getOpponentPlayer().getGodStrategy();
                opponentStrategy.setCannotMoveUp(true);
                logger.info(getName() + " Strategy: Opponent's movement up is now restricted");
            } else {
                // If Athena did not move up, ensure opponent can move up
                GodStrategy opponentStrategy = game.getOpponentPlayer().getGodStrategy();
                opponentStrategy.setCannotMoveUp(false);
                logger.info(getName() + " Strategy: Opponent's movement up is not restricted");
            }

            logger.info(getName() + " Strategy: Transitioned to MOVE phase for " + game.getCurrentPlayer().getName());
        } else {
            // Handle unexpected phases gracefully
            logger.severe(getName() + " Strategy: nextPhase called in unexpected phase: " + currentPhase);
            throw new Exception("Athena's nextPhase called in unexpected phase: " + currentPhase);
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Athena's state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called");
        // Reset Athena's state
        hasMovedUp = false;
        strategyState.clear();
        super.playerEndsTurn(game);
    }

    /**
     * Athena's strategy manages restrictions on opponent's movement.
     * This method is used by Athena to enforce or lift movement restrictions.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Athena's strategy does not utilize this method for itself
        // It enforces restrictions on the opponent
        // Hence, no action is needed here
    }
}
