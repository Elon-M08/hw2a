package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Game class manages the core logic of the Santorini game, including player turns,
 * board state, and victory conditions. It interfaces with the Board and Player classes
 * to execute game actions and determine the game's progress.
 */
public class Game {
    private Player playerA;
    private Player playerB;
    private Board board;
    private Player currentPlayer;
    private Scanner scanner;
    private boolean gameEnded = false;


    // Enum to represent the current phase of the game
    public enum GamePhase {
        PLACEMENT,
        MOVE,
        BUILD
    }
    private GamePhase currentPhase = GamePhase.PLACEMENT;
    private int workersPlaced = 0; // To track the number of workers placed
    private Worker selectedWorker = null;

    /**
     * Default constructor initializing the game.
     */
    public Game() {
        initializeGame();
    }

    /**
     * Initializes the game by setting up the board and players.
     */
    private void initializeGame() {
        board = new Board();

        // Initialize players without placing workers
        playerA = new Player("Player A");
        playerB = new Player("Player B");

        // Player A always starts
        currentPlayer = playerA;
        currentPhase = GamePhase.PLACEMENT;
        workersPlaced = 0;
        selectedWorker = null;
        gameEnded = false;
    }

    /**
     * Retrieves the current player.
     *
     * @return The Player whose turn it is.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Switches the current player after each turn.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerA) ? playerB : playerA;
    }

    /**
     * Checks if the current player has won by moving a worker to a level-3 tower.
     *
     * @return True if the current player has won; false otherwise.
     */
    public boolean checkVictory() {
        for (Worker worker : currentPlayer.getWorkers()) {
            int x = worker.getX();
            int y = worker.getY();

            // Check if the worker moved up to level 3
            if (board.getTowerHeight(x, y) == 3) {
                System.out.println(currentPlayer.getName() + " wins!");
                gameEnded = true;
                return true;
            }
        }
        return false;
    }
/**
     * Places a worker on the board during the placement phase.
     *
     * @param x The X-coordinate to place the worker.
     * @param y The Y-coordinate to place the worker.
     * @return True if the placement was successful.
     * @throws Exception If the placement is invalid or out of sequence.
     */
    public boolean placeWorker(int x, int y) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.PLACEMENT) {
            throw new Exception("Not in the placement phase.");
        }

        // Each player places two workers
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

        // Switch player after each worker placement
        if (workersPlaced % 2 == 0) {
            switchPlayer();
        }

        // Check if all workers have been placed to move to the next phase
        if (playerA.getWorkers().size() == 2 && playerB.getWorkers().size() == 2) {
            currentPhase = GamePhase.MOVE;
        }

        return true;
    }

    /**
     * Moves a worker during the move phase.
     *
     * @param workerIndex The index of the worker to move.
     * @param moveX       The X-coordinate to move to.
     * @param moveY       The Y-coordinate to move to.
     * @return True if the move was successful.
     * @throws Exception If the move is invalid or out of sequence.
     */
    public boolean moveWorker(int workerIndex, int moveX, int moveY) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.MOVE) {
            throw new Exception("Not in the move phase.");
        }

        selectedWorker = currentPlayer.getWorker(workerIndex);
        if (selectedWorker == null) {
            throw new Exception("Invalid worker selection.");
        }

        boolean moveSuccess = board.moveWorker(selectedWorker.getX(), selectedWorker.getY(), moveX, moveY);
        if (!moveSuccess) {
            throw new Exception("Invalid move. Try again.");
        }

        // Check for victory after move
        if (checkVictory()) {
            return true;
        }

        currentPhase = GamePhase.BUILD;
        return true;
    }

    /**
     * Builds on the board during the build phase.
     *
     * @param buildX The X-coordinate to build.
     * @param buildY The Y-coordinate to build.
     * @return True if the build was successful.
     * @throws Exception If the build is invalid or out of sequence.
     */
    public boolean build(int buildX, int buildY) throws Exception {
        if (gameEnded) {
            throw new Exception("Game has ended.");
        }

        if (currentPhase != GamePhase.BUILD) {
            throw new Exception("Not in the build phase.");
        }

        if (selectedWorker == null) {
            throw new Exception("No worker has been moved this turn.");
        }

        boolean buildSuccess = board.build(selectedWorker,buildX, buildY);
        if (!buildSuccess) {
            throw new Exception("Invalid build. Try again.");
        }

        // Reset for next player's turn
        selectedWorker = null;
        currentPhase = GamePhase.MOVE;
        switchPlayer();
        return true;
    }

    /**
     * Retrieves the game board.
     *
     * @return The current Board instance.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Retrieves a list of all workers in the game.
     *
     * @return A List of Worker instances.
     */
    public List<Worker> getAllWorkers() {
        List<Worker> workers = new ArrayList<>();
        workers.addAll(playerA.getWorkers());
        workers.addAll(playerB.getWorkers());
        return workers;
    }

    /**
     * Displays the current state of the game board in the console.
     *
     * This method outputs a visual representation of the 5x5 board, showing each
     * field's tower height and occupancy status. For each field:
     * - The tower height (0–3) or dome (4) is shown as an integer.
     * - The occupancy status is represented by the first letter of the player's name
     *   (e.g., "PA" for Player A) if occupied by a worker or "-" if unoccupied.
     *
     * Example output:
     * ```
     *    0  1  2  3  4
     * 0  0- 1PA 0- 0- 2-
     * 1  0- 1- 2PB 0- 3PC
     * ...
     * ```
     */
    public void displayBoard() {
        System.out.println("\nCurrent Board:");
        System.out.print("   ");
        for (int j = 0; j < 5; j++) {
            System.out.print(j + "  ");
        }
        System.out.println();
        for (int i = 0; i < 5; i++) {
            System.out.print(i + "  ");
            for (int j = 0; j < 5; j++) {
                int height = board.getTowerHeight(i, j);
                Worker worker = board.getWorkerAt(i, j);
                String occupied = (worker != null) ? worker.getOwner().getName().substring(0, 2) : "- ";
                System.out.print(height + occupied + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Helper method to safely get an integer from the user.
     *
     * @param prompt The prompt message to display.
     * @return The integer input by the user.
     */
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();  // Clear invalid input
            }
        }
    }

    /**
     * Helper method to get a validated integer input within a specified range.
     *
     * @param prompt The prompt message to display.
     * @param min    The minimum acceptable value.
     * @param max    The maximum acceptable value.
     * @return The validated integer input.
     */
    private int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            int input = getIntInput(prompt);
            if (input >= min && input <= max) {
                return input;
            }
            System.out.println("Invalid input. Please enter a value between " + min + " and " + max + ".");
        }
    }
     /**
     * Checks if the game has ended.
     *
     * @return True if the game has ended; false otherwise.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Retrieves the current phase of the game.
     *
     * @return The current GamePhase.
     */
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    /**
     * Runs the game loop, handling player turns and game progression.
     */
    public void run() {
        System.out.println("Welcome to the Santorini game!");
    
        while (true) {
            while (!gameEnded) {
                // Display the current board state
                displayBoard();
    
                System.out.println(currentPlayer.getName() + "'s turn:");
    
                try {
                    switch (currentPhase) {
                        case PLACEMENT:
                            // Get input for worker placement
                            int placeX = getValidatedInput("Enter placement position X (0-4): ", 0, 4);
                            int placeY = getValidatedInput("Enter placement position Y (0-4): ", 0, 4);
    
                            placeWorker(placeX, placeY);
                            break;
    
                        case MOVE:
                            // Get input for moving a worker
                            int workerIndex = getValidatedInput("Select a worker (0 or 1): ", 0, 1);
                            int moveX = getValidatedInput("Enter move position X (0-4): ", 0, 4);
                            int moveY = getValidatedInput("Enter move position Y (0-4): ", 0, 4);
    
                            moveWorker(workerIndex, moveX, moveY);
                            break;
    
                        case BUILD:
                            // Get input for building
                            int buildX = getValidatedInput("Enter build position X (0-4): ", 0, 4);
                            int buildY = getValidatedInput("Enter build position Y (0-4): ", 0, 4);
    
                            build(buildX, buildY);
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
    
                // Check if there is a winner
                if (gameEnded) {
                    System.out.println(currentPlayer.getName() + " wins!");
                    break;
                }
            }
    
            // Restart option
            System.out.println("Game over! Do you want to play again? (yes/no)");
            String restart = scanner.next().toLowerCase();
            if (!restart.equals("yes")) {
                break;
            }
    
            // Restart game
            initializeGame();
        }
    
        System.out.println("Thanks for playing!");
    }
    
}