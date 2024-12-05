// src/main/java/org/example/gods/ArtemisGodStrategy.java
package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;
import java.util.Map;

/**
 * Artemis's Strategy Implementation.
 * Artemis allows an additional move, but not back to the initial space.
 */
public class ArtemisGodStrategy extends AbstractGodStrategy {

    private boolean extraMoveAvailable = false;
    private int initialX = -1;
    private int initialY = -1;

    @Override
    public String getName() {
        return "Artemis";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        logger.info(getName() + " Strategy: move called");

        if (!extraMoveAvailable) {
            // First move
            boolean moveSuccess = super.move(game, worker, x, y);
            if (moveSuccess) {
                extraMoveAvailable = true;
                initialX = worker.getX();
                initialY = worker.getY();
                strategyState.put("extraMoveAvailable", true);
                strategyState.put("initialX", initialX);
                strategyState.put("initialY", initialY);
                logger.info(getName() + " Strategy: First move completed, extra move available");
            }
            return moveSuccess;
        } else {
            // Second move
            if (x == initialX && y == initialY) {
                throw new Exception("Cannot move back to the initial space.");
            }
            boolean moveSuccess = super.move(game, worker, x, y);
            if (moveSuccess) {
                extraMoveAvailable = false;
                strategyState.put("extraMoveAvailable", false);
                logger.info(getName() + " Strategy: Second move completed");
            }
            return moveSuccess;
        }
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        logger.info(getName() + " Strategy: nextPhase called");
        if (extraMoveAvailable) {
            // Awaiting second move
            // The frontend should handle prompting the player for the extra move
        } else {
            // Proceed to build phase
            super.nextPhase(game);
        }
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        logger.info(getName() + " Strategy: playerEndsTurn called");
        // Reset Artemis's state
        extraMoveAvailable = false;
        initialX = -1;
        initialY = -1;
        strategyState.clear();
        super.playerEndsTurn(game);
    }

    @Override
    public void setCannotMoveUp(boolean cannotMoveUp) {
        // Artemis's strategy does not utilize this method
        // Do nothing or implement if necessary
    }
}
