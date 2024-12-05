package org.example.gods;

import org.example.Game;
import org.example.Worker;
import java.util.*;

public class PanGodStrategy implements GodStrategy {

    @Override
    public String getName() {
        return "Pan";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        int fromX = worker.getX();
        int fromY = worker.getY();
        int fromHeight = game.getBoard().getTowerHeight(fromX, fromY);

        // Perform the default move
        boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
        if (!moveSuccess) {
            throw new Exception("Invalid move. Try again.");
        }

        int toHeight = game.getBoard().getTowerHeight(x, y);
        game.setPreviousHeight(worker, fromHeight);

        if (fromHeight - toHeight >= 2) {
            game.setGameEnded(true);
            game.setWinner(worker.getOwner().getName());
        }

        return true;
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        return game.defaultBuild(worker, x, y);
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        if (game.isGameEnded()) return;

        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            game.setSelectedWorker(null);
            game.setCurrentPhase(Game.GamePhase.MOVE);
            game.switchPlayer();
        }
    }

    @Override
    public boolean checkVictory(Game game, Worker worker) throws Exception {
        int previousHeight = game.getPreviousHeight(worker);
        int currentHeight = game.getBoard().getTowerHeight(worker.getX(), worker.getY());

        if (previousHeight - currentHeight >= 2) {
            game.setGameEnded(true);
            game.setWinner(worker.getOwner().getName());
            return true;
        }

        return game.defaultCheckVictory(worker);
    }

    @Override
    public List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception {
        DefaultGodStrategy defaultStrategy = new DefaultGodStrategy();
        return defaultStrategy.getSelectableMoveCells(game, worker);
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        DefaultGodStrategy defaultStrategy = new DefaultGodStrategy();
        return defaultStrategy.getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        return null; // Pan does not maintain extra state
    }
}
