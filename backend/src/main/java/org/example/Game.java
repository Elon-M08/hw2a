// src/main/java/org/example/Game.java
package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.gods.*;

/**
 * The Game class encapsulates the entire state and logic of the Santorini game.
 * It manages players, workers, the board, game phases, and interactions with God strategies.
 */
public class Game {
    private Player playerA;
    private Player playerB;
    private String winner; 
    private Board board;
    private Player currentPlayer;
    private boolean gameEnded = false;
    private Map<Worker, Integer> previousHeights;

    public enum GamePhase {
        PLACEMENT,
        MOVE,
        BUILD,
        BUILD_AFTER_MOVE,
        END_TURN // Added END_TURN phase
    }

    protected GamePhase currentPhase = GamePhase.PLACEMENT;
    protected int workersPlaced = 0;
    protected Worker selectedWorker = null;

    public Game() {
        initializeGame(new DefaultGodStrategy(), new DefaultGodStrategy());
    }

    public Game(GodStrategy playerAStrategy, GodStrategy playerBStrategy) {
        initializeGame(playerAStrategy, playerBStrategy);
    }

    private void initializeGame(GodStrategy playerAStrategy, GodStrategy playerBStrategy) {
        board = new Board();
        previousHeights = new HashMap<>();

        playerA = new Player("Player A", playerAStrategy);
        playerB = new Player("Player B", playerBStrategy);

        currentPlayer = playerA;
        currentPhase = GamePhase.PLACEMENT;
        workersPlaced = 0;
        selectedWorker = null;
        gameEnded = false;
    }

    // Method to create GodStrategy based on god name
    public static GodStrategy createGodStrategy(String godName) {
        switch (godName) {
            case "Apollo":
                return new ApolloGodStrategy();
            case "Artemis":
                return new ArtemisGodStrategy();
            case "Athena":
                return new AthenaGodStrategy();
            // Add other God strategies as needed
            default:
                return new DefaultGodStrategy();
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return (currentPlayer == playerA) ? playerB : playerA;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == playerA) ? playerB : playerA;
    }

    public boolean checkVictory() throws Exception {
        for (Worker worker : currentPlayer.getWorkers()) {
            if (currentPlayer.getGodStrategy().checkVictory(this, worker)) {
                System.out.println(currentPlayer.getName() + " wins!");
                gameEnded = true;
                winner = currentPlayer.getName();
                return true;
            }
        }
        return false;
    }

    public void setPreviousHeight(Worker worker, int height) {
        previousHeights.put(worker, height);
    }

    public int getPreviousHeight(Worker worker) {
        return previousHeights.getOrDefault(worker, 0);
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    /**
     * Safely retrieves selectable move cells by delegating to the current player's strategy.
     * Prevents recursion by ensuring strategies do not call back into these methods.
     */
    public List<Map<String, Integer>> getSelectableMoveCells(int workerIndex) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.MOVE) {
            throw new Exception("Not in the move phase.");
        }

        Worker worker = currentPlayer.getWorkers().get(workerIndex);
        if (worker == null) {
            throw new Exception("Invalid worker selection.");
        }

        GodStrategy strategy = currentPlayer.getGodStrategy();
        if (strategy == null) {
            throw new Exception("GodStrategy is not set for the current player.");
        }

        List<Map<String, Integer>> selectableCells = strategy.getSelectableMoveCells(this, worker);
        if (selectableCells == null) {
            throw new Exception("Selectable move cells returned null.");
        }

        return selectableCells;
    }

    /**
     * Safely retrieves selectable build cells by delegating to the current player's strategy.
     * Prevents recursion by ensuring strategies do not call back into these methods.
     */
    public List<Map<String, Integer>> getSelectableBuildCells(int workerIndex) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.BUILD && currentPhase != GamePhase.BUILD_AFTER_MOVE) {
            throw new Exception("Not in the build phase.");
        }

        Worker worker = currentPlayer.getWorkers().get(workerIndex);
        if (worker == null) {
            throw new Exception("Invalid worker selection.");
        }

        GodStrategy strategy = currentPlayer.getGodStrategy();
        if (strategy == null) {
            throw new Exception("GodStrategy is not set for the current player.");
        }

        List<Map<String, Integer>> selectableBuildCells = strategy.getSelectableBuildCells(this, worker);
        if (selectableBuildCells == null) {
            throw new Exception("Selectable build cells returned null.");
        }

        return selectableBuildCells;
    }

    public boolean placeWorker(int x, int y) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.PLACEMENT) {
            throw new Exception("Not in the placement phase.");
        }

        if (currentPlayer.getWorkers().size() >= 2) {
            throw new Exception("All workers have been placed for " + currentPlayer.getName());
        }

        Worker newWorker = new Worker(currentPlayer, x, y);
        boolean placed = board.placeWorker(x, y, newWorker);
        if (!placed) {
            throw new Exception("Failed to place worker. Invalid position or already occupied.");
        }

        currentPlayer.addWorker(newWorker);
        workersPlaced++;

        if (workersPlaced % 2 == 0) {
            switchPlayer();
        }

        if (playerA.getWorkers().size() == 2 && playerB.getWorkers().size() == 2) {
            currentPhase = GamePhase.MOVE;
        }

        return true;
    }

    public boolean moveWorker(int workerIndex, int moveX, int moveY) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.MOVE) {
            throw new Exception("Not in the move phase.");
        }

        selectedWorker = currentPlayer.getWorkers().get(workerIndex);
        if (selectedWorker == null) {
            throw new Exception("Invalid worker selection.");
        }

        boolean moveSuccess = currentPlayer.getGodStrategy().move(this, selectedWorker, moveX, moveY);
        if (!moveSuccess) {
            throw new Exception("Invalid move. Try again.");
        }

        if (currentPlayer.getGodStrategy().checkVictory(this, selectedWorker)) {
            System.out.println(currentPlayer.getName() + " wins!");
            gameEnded = true;
            winner = currentPlayer.getName();
            return true;
        }

        // Let the strategy determine the next phase
        currentPlayer.getGodStrategy().nextPhase(this);
        return true;
    }

    public boolean build(int buildX, int buildY) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.BUILD && currentPhase != GamePhase.BUILD_AFTER_MOVE) {
            throw new Exception("Not in the build phase.");
        }

        if (selectedWorker == null) {
            throw new Exception("No worker has been moved this turn.");
        }

        boolean buildSuccess = currentPlayer.getGodStrategy().build(this, selectedWorker, buildX, buildY);
        if (!buildSuccess) {
            throw new Exception("Invalid build. Try again.");
        }

        // Let the strategy determine the next phase
        currentPlayer.getGodStrategy().nextPhase(this);

        return true;
    }

    public Board getBoard() {
        return board;
    }

    public List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();
        workers.addAll(playerA.getWorkers());
        workers.addAll(playerB.getWorkers());
        return workers;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        this.currentPhase = phase;
    }

    public void setSelectedWorker(Worker worker) {
        this.selectedWorker = worker;
    }

    public boolean defaultMoveWorker(Worker worker, int x, int y) throws Exception {
        int fromX = worker.getX();
        int fromY = worker.getY();

        int fromHeight = board.getTowerHeight(fromX, fromY);
        previousHeights.put(worker, fromHeight);

        boolean moveSuccess = board.moveWorker(fromX, fromY, x, y);
        if (!moveSuccess) {
            return false;
        }
        return true;
    }

    public boolean defaultBuild(Worker worker, int x, int y) throws Exception {
        boolean buildSuccess = board.build(x, y);
        if (!buildSuccess) {
            return false;
        }
        return true;
    }

    public boolean defaultCheckVictory(Worker worker) {
        int x = worker.getX();
        int y = worker.getY();

        if (board.getTowerHeight(x, y) == 3) {
            gameEnded = true;
            return true;
        }
        return false;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public String getWinner() {
        return winner;
    }
}
