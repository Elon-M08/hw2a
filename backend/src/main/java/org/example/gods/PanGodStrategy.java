package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.Map;
import java.util.logging.Logger;

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

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: Attempting move from (" + worker.getX() + ", " + worker.getY() + ") to (" + x + ", " + y + ")");
        
        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();
        int currentHeight = board.getTowerHeight(fromX, fromY);

        // Use the default move logic first to ensure validity.
        boolean moveSuccess = super.move(game, worker, x, y);
        if (!moveSuccess) {
            // If the default move logic fails, just return false rather than throwing an exception.
            // The `game.moveWorker()` method will handle exceptions and state accordingly.
            logger.warning(getName() + " Strategy: move failed due to standard rules.");
            return false;
        }

        // If we got here, the move succeeded according to default rules.
        int newHeight = board.getTowerHeight(x, y);
        lastMoveHeightDifference = newHeight - currentHeight;
        strategyState.put("lastMoveHeightDifference", lastMoveHeightDifference);

        logger.info(getName() + " Strategy: Move succeeded with height difference of " + lastMoveHeightDifference);
        return true;
    }

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
        // Reset Pan's last move height difference at end of turn
        lastMoveHeightDifference = 0;
        strategyState.put("lastMoveHeightDifference", lastMoveHeightDifference);
        super.playerEndsTurn(game);
    }

    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Pan's strategy does not utilize this method, do nothing.
    }
}
