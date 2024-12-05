package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinotaurGodStrategy extends DefaultGodStrategy {

    @Override
    public String getName() {
        return "Minotaur";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        int fromX = worker.getX();
        int fromY = worker.getY();

        Board board = game.getBoard();

        // Check if the move is valid (adjacent)
        if (Math.abs(x - fromX) > 1 || Math.abs(y - fromY) > 1) {
            throw new Exception("Invalid move: Must move to an adjacent space.");
        }

        if (!board.isWithinBounds(x, y)) {
            throw new Exception("Invalid move: Target position is out of bounds.");
        }

        int currentHeight = board.getTowerHeight(fromX, fromY);
        int targetHeight = board.getTowerHeight(x, y);

        if (targetHeight - currentHeight > 1) {
            throw new Exception("Invalid move: Cannot move up more than one level.");
        }

        Worker targetWorker = board.getWorkerAt(x, y);

        if (targetWorker == null) {
            // Space is empty, proceed as normal
            return game.defaultMoveWorker(worker, x, y);
        } else if (!targetWorker.getOwner().equals(worker.getOwner())) {
            // Space is occupied by opponent's worker
            // Try to push the opponent's worker
            int deltaX = x - fromX;
            int deltaY = y - fromY;
            int pushX = x + deltaX;
            int pushY = y + deltaY;

            if (!board.isWithinBounds(pushX, pushY)) {
                throw new Exception("Invalid move: Cannot push opponent's worker out of bounds.");
            }

            if (board.isOccupied(pushX, pushY)) {
                throw new Exception("Invalid move: Cannot push opponent's worker into an occupied space.");
            }

            // Perform the push
            board.moveWorker(x, y, pushX, pushY);
            // Move own worker into the vacated space
            board.moveWorker(fromX, fromY, x, y);

            return true;

        } else {
            throw new Exception("Invalid move: Cannot move into your own worker's space.");
        }
    }

    @Override
    public List<Map<String, Integer>> getSelectableMoveCells(Game game, Worker worker) throws Exception {
        List<Map<String, Integer>> selectableCells = new ArrayList<>();
        int x = worker.getX();
        int y = worker.getY();

        Board board = game.getBoard();
        int currentHeight = board.getTowerHeight(x, y);

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},         { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (!board.isWithinBounds(newX, newY)) {
                continue; // Skip out-of-bounds positions
            }

            int targetHeight = board.getTowerHeight(newX, newY);

            if (targetHeight - currentHeight > 1) {
                continue; // Cannot move up more than one level
            }

            Worker targetWorker = board.getWorkerAt(newX, newY);

            if (targetWorker == null) {
                // Empty space, can move if not a dome
                if (targetHeight < 4) {
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                }
            } else if (!targetWorker.getOwner().equals(worker.getOwner())) {
                // Occupied by opponent's worker
                int deltaX = dir[0];
                int deltaY = dir[1];
                int pushX = newX + deltaX;
                int pushY = newY + deltaY;

                if (!board.isWithinBounds(pushX, pushY)) {
                    continue; // Cannot push out of bounds
                }

                if (!board.isOccupied(pushX, pushY) && board.getTowerHeight(pushX, pushY) < 4) {
                    // Can push opponent's worker
                    Map<String, Integer> cell = new HashMap<>();
                    cell.put("x", newX);
                    cell.put("y", newY);
                    selectableCells.add(cell);
                }
            }
            // Else, cannot move into own worker's space
        }

        return selectableCells;
    }
    @Override
    public Map<String, Object> getStrategyState() {
        return Map.of("name", "Minotaur", "canForceMove", true);
    }

}
