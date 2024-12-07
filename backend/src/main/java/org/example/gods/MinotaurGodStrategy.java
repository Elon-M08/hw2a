// src/main/java/org/example/gods/MinotaurGodStrategy.java
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
 * Minotaur's Strategy Implementation.
 * Minotaur allows a player to move their worker into an opponent's space by forcing their opponent's worker to the space Minotaur's worker vacated.
 */
public class MinotaurGodStrategy extends DefaultGodStrategy {
    private static final Logger logger = Logger.getLogger(MinotaurGodStrategy.class.getName());

    // Tracks if the player has performed a swap during this turn
    private boolean hasSwapped = false;

    // Stores the direction of the push to validate the swap
    private int pushDeltaX = 0;
    private int pushDeltaY = 0;

    @Override
    public String getName() {
        return "Minotaur";
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = super.getStrategyState();
        state.put("hasSwapped", hasSwapped);
        state.put("pushDeltaX", pushDeltaX);
        state.put("pushDeltaY", pushDeltaY);
        return state;
    }

    /**
     * Overrides the getSelectableMoveCells method to include opponent's cells.
     * This allows Minotaur to attempt to move into cells occupied by opponent workers.
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
                    // Empty cell is always a valid move
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                } else if (!targetWorker.getOwner().equals(worker.getOwner())) {
                    // Opponent's worker: check if push is possible
                    int pushX = newX + dx;
                    int pushY = newY + dy;

                    if (!board.isWithinBounds(pushX, pushY)) continue; // Cannot push out of bounds
                    if (board.isOccupied(pushX, pushY)) continue; // Cannot push into occupied cell
                    if (board.getTowerHeight(pushX, pushY) >= 4) continue; // Cannot push into a dome

                    // Add opponent's cell as selectable
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
     * Overrides the move method to implement Minotaur's special ability.
     * Allows moving into an opponent's space by pushing their worker.
     */
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called to (" + x + ", " + y + ")");

        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();

        // Check if the move is adjacent
        if (Math.abs(x - fromX) > 1 || Math.abs(y - fromY) > 1) {
            logger.warning(getName() + " Strategy: Invalid move: Must move to an adjacent space.");
            throw new Exception("Invalid move: Must move to an adjacent space.");
        }

        if (!board.isWithinBounds(x, y)) {
            logger.warning(getName() + " Strategy: Invalid move: Target position is out of bounds.");
            throw new Exception("Invalid move: Target position is out of bounds.");
        }

        int currentHeight = board.getTowerHeight(fromX, fromY);
        int targetHeight = board.getTowerHeight(x, y);

        if (targetHeight - currentHeight > 1) {
            logger.warning(getName() + " Strategy: Invalid move: Cannot move up more than one level.");
            throw new Exception("Invalid move: Cannot move up more than one level.");
        }

        Worker targetWorker = board.getWorkerAt(x, y);

        if (targetWorker == null) {
            // Space is empty, proceed as normal
            boolean moveSuccess = super.move(game, worker, x, y);
            if (moveSuccess) {
                logger.info(getName() + " Strategy: Standard move completed to (" + x + ", " + y + ").");
            }
            return moveSuccess;
        } else if (!targetWorker.getOwner().equals(worker.getOwner())) {
            // Space is occupied by opponent's worker, attempt to push
            int deltaX = x - fromX;
            int deltaY = y - fromY;
            int pushX = x + deltaX;
            int pushY = y + deltaY;

            logger.info(getName() + " Strategy: Attempting to push opponent's worker from (" + x + ", " + y + ") to (" + pushX + ", " + pushY + ").");

            if (!board.isWithinBounds(pushX, pushY)) {
                logger.warning(getName() + " Strategy: Cannot push opponent's worker out of bounds.");
                throw new Exception("Invalid move: Cannot push opponent's worker out of bounds.");
            }

            if (board.isOccupied(pushX, pushY)) {
                logger.warning(getName() + " Strategy: Cannot push opponent's worker into an occupied space.");
                throw new Exception("Invalid move: Cannot push opponent's worker into an occupied space.");
            }

            if (board.getTowerHeight(pushX, pushY) >= 4) {
                logger.warning(getName() + " Strategy: Cannot push opponent's worker into a dome.");
                throw new Exception("Invalid move: Cannot push opponent's worker into a dome.");
            }

            // Perform the push
            board.moveWorker(x, y, pushX, pushY);
            logger.info(getName() + " Strategy: Opponent's worker pushed to (" + pushX + ", " + pushY + ").");

            // Move own worker into the vacated space
            boolean moveSuccess = super.move(game, worker, x, y);
            if (moveSuccess) {
                hasSwapped = true;
                pushDeltaX = deltaX;
                pushDeltaY = deltaY;
                strategyState.put("hasSwapped", hasSwapped);
                strategyState.put("pushDeltaX", pushDeltaX);
                strategyState.put("pushDeltaY", pushDeltaY);
                logger.info(getName() + " Strategy: Swapped move completed to (" + x + ", " + y + ").");
            }
            return moveSuccess;
        } else {
            logger.warning(getName() + " Strategy: Invalid move: Cannot move into your own worker's space.");
            throw new Exception("Invalid move: Cannot move into your own worker's space.");
        }
    }

    /**
     * Overrides the nextPhase method to transition to the build phase after moving.
     */
    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called.");

        if (hasSwapped) {
            // After a swap, proceed to build phase
            super.nextPhase(game);
            logger.info(getName() + " Strategy: Proceeding to build phase after swap.");
        } else {
            // If no swap, proceed normally
            super.nextPhase(game);
            logger.info(getName() + " Strategy: Proceeding to build phase.");
        }

        // Reset swap state for the next turn
        hasSwapped = false;
        pushDeltaX = 0;
        pushDeltaY = 0;
        strategyState.put("hasSwapped", hasSwapped);
        strategyState.put("pushDeltaX", pushDeltaX);
        strategyState.put("pushDeltaY", pushDeltaY);
    }

    /**
     * Overrides the playerEndsTurn method to reset Minotaur's state.
     */
    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called.");
        // Reset Minotaur's swap state
        hasSwapped = false;
        pushDeltaX = 0;
        pushDeltaY = 0;
        strategyState.put("hasSwapped", hasSwapped);
        strategyState.put("pushDeltaX", pushDeltaX);
        strategyState.put("pushDeltaY", pushDeltaY);
        // Delegate to superclass to handle any additional reset logic
        super.playerEndsTurn(game);
    }

    /**
     * Minotaur's strategy does not utilize the setCannotMoveUp method.
     */
    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Minotaur's strategy does not utilize this method
        // Do nothing
    }
}
