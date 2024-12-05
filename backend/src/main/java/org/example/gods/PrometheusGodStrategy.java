package org.example.gods;

import org.example.Game;
import org.example.Worker;
import org.example.Board;
import java.util.*;

public class PrometheusGodStrategy implements GodStrategy {

    private boolean builtBeforeMove = false;
    private boolean canMoveUp = true;

    @Override
    public String getName() {
        return "Prometheus";
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

        if (builtBeforeMove && toHeight > fromHeight) {
            throw new Exception("Cannot move up after building before moving.");
        }

        boolean moveSuccess = game.defaultMoveWorker(worker, x, y);
        if (!moveSuccess) {
            throw new Exception("Invalid move. Try again.");
        }

        return true;
    }

    @Override
    public boolean build(Game game, Worker worker, int x, int y) throws Exception {
        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            // Building before moving
            if (builtBeforeMove) {
                throw new Exception("You have already built before moving.");
            }
            boolean buildSuccess = game.defaultBuild(worker, x, y);
            if (buildSuccess) {
                builtBeforeMove = true;
                canMoveUp = false;
            }
            return buildSuccess;
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            // Building after moving
            if (!builtBeforeMove) {
                throw new Exception("You must build before moving to build after moving.");
            }
            boolean buildSuccess = game.defaultBuild(worker, x, y);
            if (buildSuccess) {
                // Reset state after completing both builds
                builtBeforeMove = false;
                canMoveUp = true;
            }
            return buildSuccess;
        } else {
            throw new Exception("Invalid game phase for building.");
        }
    }

    @Override
    public void nextPhase(Game game) throws Exception {
        if (game.isGameEnded()) {
            return;
        }

        if (game.getCurrentPhase() == Game.GamePhase.MOVE) {
            if (builtBeforeMove) {
                // Proceed to MOVE phase after building before moving
                game.setCurrentPhase(Game.GamePhase.MOVE);
            } else {
                // Proceed to BUILD phase if no pre-move build
                game.setCurrentPhase(Game.GamePhase.BUILD);
            }
        } else if (game.getCurrentPhase() == Game.GamePhase.BUILD) {
            // End turn
            builtBeforeMove = false;
            canMoveUp = true;
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

            if (board.isOccupied(moveX, moveY)) {
                continue;
            }

            int targetHeight = board.getTowerHeight(moveX, moveY);

            if (targetHeight - currentHeight > 1 || targetHeight >= 4) {
                continue;
            }

            if (builtBeforeMove && targetHeight > currentHeight) {
                continue; // Cannot move up after building before moving
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
        state.put("canBuildBeforeMove", !builtBeforeMove && game.getCurrentPhase() == Game.GamePhase.MOVE);
        state.put("builtBeforeMove", builtBeforeMove);
        return state;
    }

    @Override
    public void playerEndsTurn(Game game) throws Exception {
        builtBeforeMove = false;
        canMoveUp = true;
        game.setSelectedWorker(null);
        game.setCurrentPhase(Game.GamePhase.MOVE);
        game.switchPlayer();
    }
}
