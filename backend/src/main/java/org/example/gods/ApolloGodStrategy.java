// src/main/java/org/example/gods/ApolloGodStrategy.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.ArrayList; // Added import
import java.util.HashMap;
import java.util.List; // Added import
import java.util.Map;
import java.util.logging.Logger;

/**
 * Apollo's Strategy Implementation.
 * Apollo allows a player to move their worker into an opponent's space by forcing their opponent's worker to the space Apollo's worker vacated.
 */
public class ApolloGodStrategy extends AbstractGodStrategy {
    private static final Logger logger = Logger.getLogger(ApolloGodStrategy.class.getName());

    // Tracks if the player has performed a swap during this turn
    private boolean hasSwapped = false;

    @Override
    public String getName() {
        return "Apollo";
    }

    /**
     * Overrides the getSelectableMoveCells method to include opponent's cells for swapping.
     * Apollo can move into an opponent's cell by swapping positions.
     */
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

                // Check height difference
                if (targetHeight - currentHeight > 1) continue;

                Worker targetWorker = board.getWorkerAt(newX, newY);

                if (targetWorker == null) {
                    // Empty cell is a valid move
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                } else if (!targetWorker.getOwner().equals(worker.getOwner())) {
                    // Opponent's worker: Apollo can swap
                    // Ensure that swapping does not push the opponent's worker into an invalid state
                    // For Apollo, no push is involved; it's a direct swap

                    // Check if the current cell (x, y) is a valid cell to receive the opponent's worker
                    // Typically, swapping does not have additional constraints besides adjacency and height difference
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                }
                // Else, occupied by own worker: not selectable
            }
        }

        logger.info(getName() + " Strategy: Selectable move cells determined");
        return selectableCells;
    }

    /**
     * Overrides the move method to implement Apollo's special ability.
     * Allows swapping with an opponent's worker.
     */
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called to (" + x + ", " + y + ")");

        Board board = game.getBoard();
        Worker targetWorker = board.getWorkerAt(x, y);

        if (targetWorker == null) {
            // Standard move
            boolean moveSuccess = super.move(game, worker, x, y);
            if (moveSuccess) {
                logger.info(getName() + " Strategy: Standard move completed");
            }
            return moveSuccess;
        } else {
            // Attempt to swap with opponent's worker
            if (!targetWorker.getOwner().equals(worker.getOwner())) {
                int fromX = worker.getX();
                int fromY = worker.getY();

                // Check height difference for the swap
                int currentHeight = board.getTowerHeight(fromX, fromY);
                int targetHeight = board.getTowerHeight(x, y);
                if (targetHeight - currentHeight > 1) {
                    logger.warning(getName() + " Strategy: Cannot swap: Opponent's worker is too high.");
                    throw new Exception("Cannot swap: Opponent's worker is too high.");
                }

                // Perform the swap
                boolean swapSuccess = board.swapWorkers(worker, targetWorker);
                if (swapSuccess) {
                    hasSwapped = true;
                    strategyState.put("hasSwapped", true);
                    logger.info(getName() + " Strategy: Swapped with opponent's worker at (" + x + ", " + y + ")");
                    return true;
                } else {
                    logger.warning(getName() + " Strategy: Failed to swap with opponent's worker.");
                    throw new Exception("Failed to swap with opponent's worker.");
                }
            } else {
                // Target worker belongs to the same player; invalid move
                logger.warning(getName() + " Strategy: Target cell occupied by own worker. Invalid move.");
                throw new Exception("Cannot move into a cell occupied by your own worker.");
            }
        }
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
            // After BUILD phase, end turn and switch to the next player
            game.switchPlayer();
            game.setCurrentPhase(Game.GamePhase.MOVE);
            logger.info(getName() + " Strategy: Transitioned to MOVE phase for " + game.getCurrentPlayer().getName());
        } else {
            // Handle unexpected phases gracefully
            logger.severe(getName() + " Strategy: nextPhase called in unexpected phase: " + currentPhase);
            throw new Exception("Apollo's nextPhase called in unexpected phase: " + currentPhase);
        }
    }

    /**
     * Overrides the playerEndsTurn method to reset Apollo's state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called");

        // Reset Apollo's state
        hasSwapped = false;
        strategyState.remove("hasSwapped");

        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    /**
     * Apollo's strategy does not utilize the setCannotMoveUp method.
     * It can be left empty or used if Apollo gains additional abilities in the future.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Apollo's strategy does not utilize this method
        // Do nothing
    }
}
