package org.example.gods;

import org.example.Game;
import org.example.Worker;
import java.util.*;

public class HephaestusGodStrategy implements GodStrategy {
    private boolean extraBuildAvailable = false;
    private int firstBuildX = -1;
    private int firstBuildY = -1;

    @Override
    public String getName() {
        return "Hephaestus";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        // Use default move logic
        return game.defaultMoveWorker(worker, x, y);
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        // Implement Hephaestus's special build logic
        // Hephaestus can build one additional block (up to two levels total) on top of the first build position
        if (!extraBuildAvailable) {
            // First build
            boolean success = game.defaultBuild(worker, x, y);
            if (success) {
                firstBuildX = x;
                firstBuildY = y;
                extraBuildAvailable = true;
            }
            return success;
        } else {
            // Second build on the same space, only if the current level is less than 3
            if (x == firstBuildX && y == firstBuildY) {
                int currentHeight = game.getBoard().getTowerHeight(x, y);
                if (currentHeight < 3) {
                    boolean success = game.defaultBuild(worker, x, y);
                    extraBuildAvailable = false;
                    firstBuildX = -1;
                    firstBuildY = -1;
                    return success;
                } else {
                    throw new Exception("Cannot build a second time on a tower of height 3 or higher.");
                }
            } else {
                throw new Exception("Second build must be on the same space as the first build.");
            }
        }
    }

    @Override
    public void nextPhase(Game game) {
        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            game.setCurrentPhase(Game.GamePhase.BUILD);
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            if (extraBuildAvailable) {
                // Remain in BUILD phase, waiting for second build
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
        // Use default move logic
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

            if (extraBuildAvailable) {
                // Second build must be on the same space
                if (buildX == firstBuildX && buildY == firstBuildY) {
                    // Check if current height is less than 3
                    if (game.getBoard().getTowerHeight(buildX, buildY) < 3) {
                        Map<String, Integer> cell = new HashMap<>();
                        cell.put("x", buildX);
                        cell.put("y", buildY);
                        selectableCells.add(cell);
                    }
                }
            } else {
                // First build can be on any adjacent space
                Map<String, Integer> cell = new HashMap<>();
                cell.put("x", buildX);
                cell.put("y", buildY);
                selectableCells.add(cell);
            }
        }

        return selectableCells;
    }
}
