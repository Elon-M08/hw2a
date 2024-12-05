package org.example.gods;

import org.example.Game;
import org.example.Worker;

import java.util.*;

public class DefaultGodStrategy implements GodStrategy {
    @Override
    public String getName() {
        return "Default";
    }
    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        return game.defaultMoveWorker(worker, x, y);
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        return game.defaultBuild(worker, x, y);
    }

    @Override
    public boolean checkVictory(Game game, Worker worker) throws Exception {
        return game.defaultCheckVictory(worker);
    }
    @Override
    public Map<String, Object> getStrategyState() {
        // Default strategy has no additional state
        return null;
    }
    @Override
    public void nextPhase(Game game) {
        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            // End turn
            game.setSelectedWorker(null);
            game.setCurrentPhase(Game.GamePhase.MOVE);
            game.switchPlayer();
        }
    }
    
    @Override
    public List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception {
        // Logic to determine selectable move cells
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        int x = worker.getX();
        int y = worker.getY();
        int currentHeight = game.getBoard().getTowerHeight(x, y);

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},         {0, 1},
            {1, -1},  {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (game.getBoard().isWithinBounds(newX, newY)) {
                if (game.getBoard().isValidMove(x, y, newX, newY)) {
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                }
            }
        }
        return selectableCells;
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        // Logic to determine selectable build cells
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        int x = worker.getX();
        int y = worker.getY();

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},         {0, 1},
            {1, -1},  {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int buildX = x + dir[0];
            int buildY = y + dir[1];
            if (game.getBoard().isWithinBounds(buildX, buildY)) {
                if (!game.getBoard().isOccupied(buildX, buildY) && game.getBoard().getTowerHeight(buildX, buildY) < 4) {
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", buildX);
                    cell.put("y", buildY);
                    selectableCells.add(cell);
                }
            }
        }
        return selectableCells;
    }
}
