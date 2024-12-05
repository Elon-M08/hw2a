package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;

public class AthenaGodStrategy implements GodStrategy {

    private boolean opponentCannotMoveUp = false;

    @Override
    public String getName() {
        return "Athena";
    }

    @Override
    public boolean move(Game game, Worker worker, int x, int y) throws Exception {
        Board board = game.getBoard();
        int fromX = worker.getX();
        int fromY = worker.getY();
        int fromHeight = board.getTowerHeight(fromX, fromY);
        int toHeight = board.getTowerHeight(x, y);

        if (toHeight - fromHeight > 1) {
            throw new Exception("Cannot move up more than one level.");
        }

        if (toHeight - fromHeight == 1) {
            // Moved up
            opponentCannotMoveUp = true;
        } else {
            opponentCannotMoveUp = false;
        }

        return game.defaultMoveWorker(worker, x, y);
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
            game.setSelectedWorker(null);
            game.setCurrentPhase(Game.GamePhase.MOVE);
            game.switchPlayer();
            // Apply effect to opponent
            game.getCurrentPlayer().getGodStrategy().setOpponentMovedUp(opponentCannotMoveUp);
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

            if (board.isOccupied(moveX, moveY)) {
                continue;
            }

            int targetHeight = board.getTowerHeight(moveX, moveY);

            if (targetHeight - currentHeight > 1 || targetHeight >= 4) {
                continue;
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
        return new DefaultGodStrategy().getSelectableBuildCells(game, worker);
    }

    @Override
    public Map<String, Object> getStrategyState() {
        Map<String, Object> state = new HashMap<>();
        state.put("opponentCannotMoveUp", opponentCannotMoveUp);
        return state;
    }

    // Method to be called by opponent's strategy
    public void setOpponentMovedUp(boolean value) {
        opponentCannotMoveUp = value;
    }
}
