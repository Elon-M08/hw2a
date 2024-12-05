package org.example.gods;

import org.example.Game;
import org.example.Worker;
import java.util.*;

public class ArtemisGodStrategy implements GodStrategy {

    private boolean extraMoveAvailable = false;
    private int initialX = -1;
    private int initialY = -1;

    @Override
    public String getName() {
        return "Artemis";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        if (!extraMoveAvailable) {
            // First move
            boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
            if (moveSuccess) {
                extraMoveAvailable = true;
                initialX = worker.getX();
                initialY = worker.getY();
            }
            return moveSuccess;
        } else {
            // Second move
            if (x == initialX && y == initialY) {
                throw new Exception("Cannot move back to the initial space.");
            }
            boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
            if (moveSuccess) {
                extraMoveAvailable = false;
                return true;
            } else {
                throw new Exception("Invalid move. Try again.");
            }
        }
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        if (extraMoveAvailable) {
            throw new Exception("You must finish your moves before building.");
        }
        // Use default build logic
        return game.defaultBuild(worker, x, y);
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        if (game.isGameEnded()) {
            return;
        }

        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            if (extraMoveAvailable) {
                // Wait for player to decide to move again or skip
            } else {
                game.setCurrentPhase(Game.GamePhase.BUILD);
            }
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            // End turn
            extraMoveAvailable = false;
            initialX = -1;
            initialY = -1;
            game.setSelectedWorker(null);
            game.setCurrentPhase(Game.GamePhase.MOVE);
            game.switchPlayer();
        }
    }

    @Override
    public boolean checkVictory(Game game, Worker worker) throws Exception {
        return game.defaultCheckVictory(worker);
    }

    @Override
    public List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception {
        if (!extraMoveAvailable) {
            // First move
            return new DefaultGodStrategy().getSelectableMoveCells(game, worker);
        } else {
            // Second move
            List<Map<String, Integer>> selectableCells = new DefaultGodStrategy().getSelectableMoveCells(game, worker);
            // Remove the initial space
            selectableCells.removeIf(cell -> cell.get("x") == initialX && cell.get("y") == initialY);
            return selectableCells;
        }
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        if (extraMoveAvailable) {
            // Cannot build until moves are finished
            return Collections.emptyList();
        }
        return new DefaultGodStrategy().getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = new HashMap<>();
        state.put("extraMoveAvailable", extraMoveAvailable);
        return state;
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        if (extraMoveAvailable) {
            // Player chooses to skip the second move
            extraMoveAvailable = false;
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else {
            // End turn normally
            game.setSelectedWorker(null);
            game.setCurrentPhase(Game.GamePhase.MOVE);
            game.switchPlayer();
        }
    }
}

