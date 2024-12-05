package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;

public class ApolloGodStrategy implements GodStrategy {

    @Override
    public String getName() {
        return "Apollo";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();

        if (!board.isWithinBounds(x, y)) {
            throw new Exception("Move out of bounds.");
        }

        int heightDifference = board.getTowerHeight(x, y) - board.getTowerHeight(fromX, fromY);
        if (heightDifference > 1) {
            throw new Exception("Cannot move up more than one level.");
        }

        if (board.isOccupied(x, y)) {
            Worker opponentWorker = board.getWorkerAt(x, y);
            if (opponentWorker.getOwner() == worker.getOwner()) {
                throw new Exception("Cannot move into your own worker's space.");
            } else {
                // Swap positions
                board.moveWorker(fromX, fromY, x, y);
                board.moveWorker(x, y, fromX, fromY);
                opponentWorker.setPosition(fromX, fromY);
                worker.setPosition(x, y);
            }
        } else {
            // Normal move
            boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
            if (!moveSuccess) {
                throw new Exception("Invalid move. Try again.");
            }
        }

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
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        Board board = game.getBoard();
        int x = worker.getX();
        int y = worker.getY();

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},         { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
        };

        int currentHeight = board.getTowerHeight(x, y);

        for (int[] dir : directions) {
            int moveX = x + dir[0];
            int moveY = y + dir[1];

            if (!board.isWithinBounds(moveX, moveY)) {
                continue;
            }

            int targetHeight = board.getTowerHeight(moveX, moveY);

            if (targetHeight - currentHeight > 1 || targetHeight >= 4) {
                continue;
            }

            if (board.isOccupied(moveX, moveY)) {
                Worker otherWorker = board.getWorkerAt(moveX, moveY);
                if (otherWorker.getOwner() == worker.getOwner()) {
                    continue; // Cannot move into own worker's space
                }
                // Can move into opponent's space
            }

            Map<String, Integer> cell = new HashMap<>();
            cell.put("x", moveX);
            cell.put("y", moveY);
            selectableCells.add(cell);
        }

        return selectableCells;
    }

    @Override
    public List<Map<String, Integer>> getSelectableBuildCells(Game game, Worker worker) throws Exception {
        // Use default build logic
        return new DefaultGodStrategy().getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        // Apollo does not have extra state to maintain
        return null;
    }
}
