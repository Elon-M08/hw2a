package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;

public class AtlasGodStrategy implements GodStrategy {

    private boolean buildDome = false;

    @Override
    public String getName() {
        return "Atlas";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        // Use default move logic
        return game.defaultMoveWorker(worker, x, y);
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        Board board = game.getBoard();

        if (!board.isAdjacent(worker.getX(), worker.getY(), x, y)) {
            throw new Exception("Build position must be adjacent.");
        }

        if (board.isOccupied(x, y)) {
            throw new Exception("Cannot build on an occupied space.");
        }

        if (buildDome) {
            // Build a dome regardless of current height
            board.setTowerHeight(x, y, 4);
            buildDome = false;
        } else {
            // Normal build
            game.defaultBuild(worker, x, y);
        }

        return true;
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        if (game.isGameEnded()) {
            return;
        }

        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            // End turn
            buildDome = false;
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
        return new DefaultGodStrategy().getSelectableMoveCells(game, worker);
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        return new DefaultGodStrategy().getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = new HashMap<>();
        state.put("canBuildDome", true);
        return state;
    }

    public void setBuildDome(boolean buildDome) {
        this.buildDome = buildDome;
    }
}
