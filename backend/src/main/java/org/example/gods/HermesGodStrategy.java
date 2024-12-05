package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;

public class HermesGodStrategy implements GodStrategy {

    private Set<String> visitedPositions = new HashSet<>();

    @Override
    public String getName() {
        return "Hermes";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        // Moving to the selected position directly
        worker.setPosition(x, y);
        game.getBoard().moveWorker(worker.getX(), worker.getY(), x, y);
        return true;
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        // Use default build logic
        return game.defaultBuild(worker, x, y);
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
            visitedPositions.clear();
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
        // Get all positions reachable without changing levels
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        Board board = game.getBoard();
        int startX = worker.getX();
        int startY = worker.getY();
        int currentHeight = board.getTowerHeight(startX, startY);

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visitedPositions.clear();
        visitedPositions.add(startX + "," + startY);

        while (!queue.isEmpty()) {
            int[] position = queue.poll();
            int x = position[0];
            int y = position[1];

            int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1},         { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
            };

            for (int[] dir : directions) {
                int moveX = x + dir[0];
                int moveY = y + dir[1];
                String posKey = moveX + "," + moveY;

                if (!board.isWithinBounds(moveX, moveY)) {
                    continue;
                }

                if (visitedPositions.contains(posKey)) {
                    continue;
                }

                if (board.isOccupied(moveX, moveY)) {
                    continue;
                }

                int targetHeight = board.getTowerHeight(moveX, moveY);

                if (targetHeight != currentHeight || targetHeight >= 4) {
                    continue; // Cannot move up or down and cannot move onto domes
                }

                visitedPositions.add(posKey);

                Map<String, Integer> cell = new HashMap<>();
                cell.put("x", moveX);
                cell.put("y", moveY);
                selectableCells.add(cell);

                queue.add(new int[]{moveX, moveY});
            }
        }

        return selectableCells;
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        return new DefaultGodStrategy().getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        // Hermes does not maintain extra state
        return null;
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        visitedPositions.clear();
        game.setSelectedWorker(null);
        game.setCurrentPhase(Game.GamePhase.MOVE);
        game.switchPlayer();
    }
}
