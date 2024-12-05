package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.*;

public class DemeterGodStrategy implements GodStrategy {
    private Map<String, Integer> firstBuildPosition = null;
    private boolean extraBuildAvailable = false;

    @Override
    public String getName() {
        return "Demeter";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        // Use default move logic
        return game.defaultMoveWorker(worker, x, y);
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        Board board = game.getBoard();

        if (board.isOccupied(x, y) || board.getTowerHeight(x, y) >= 4) {
            throw new Exception("Invalid build location.");
        }

        if (firstBuildPosition == null) {
            // First build
            boolean success = game.defaultBuild(worker, x, y);
            if (success) {
                firstBuildPosition = Map.of("x", x, "y", y);
                extraBuildAvailable = true;
            }
            return success;
        } else {
            // Second build
            if (firstBuildPosition.get("x") == x && firstBuildPosition.get("y") == y) {
                throw new Exception("Cannot build on the same space as the first build.");
            }
            boolean success = game.defaultBuild(worker, x, y);
            if (success) {
                extraBuildAvailable = false;
                firstBuildPosition = null;
            }
            return success;
        }
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            if (extraBuildAvailable) {
                // Remain in BUILD phase; wait for player's decision
            } else {
                // End turn
                game.setSelectedWorker(null);
                game.setCurrentPhase(Game.GamePhase.MOVE);
                game.switchPlayer();
            }
        }
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = new HashMap<>();
        state.put("extraBuildAvailable", extraBuildAvailable);
        return state;
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
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        int x = worker.getX();
        int y = worker.getY();

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},         { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (int[] dir : directions) {
            int buildX = x + dir[0];
            int buildY = y + dir[1];

            if (!game.getBoard().isWithinBounds(buildX, buildY)) {
                continue;
            }

            if (game.getBoard().isOccupied(buildX, buildY) || game.getBoard().getTowerHeight(buildX, buildY) >= 4) {
                continue;
            }

            if (firstBuildPosition != null && firstBuildPosition.get("x") == buildX && firstBuildPosition.get("y") == buildY) {
                continue;
            }

            Map<String, Integer> cell = Map.of("x", buildX, "y", buildY);
            selectableCells.add(cell);
        }

        return selectableCells;
    }

}
