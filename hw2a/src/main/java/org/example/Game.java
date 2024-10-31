package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    private Player playerA;
    private Player playerB;
    private Board board;
    private Player currentPlayer;
    private Scanner scanner;


    /**
     * Initializes a new game with a board, players, and starting conditions.
     *
     * The constructor sets up the game by:
     * - Initializing the game board and scanner for handling player input.
     * - Creating two players (`playerA` and `playerB`) with default names and initial
     *   worker positions on the board. Player A's workers are placed at `(0, 0)` and `(1, 1)`,
     *   while Player B's workers are placed at `(4, 4)` and `(3, 3)`.
     * - Setting `currentPlayer` to `playerA`, as Player A always starts the game.
     *
     * @post The game is initialized with a ready-to-play board and both players 
     *       positioned according to their starting placements.
     */
    public Game() {
        // Initialize the board and scanner for input.
        board = new Board();
        scanner = new Scanner(System.in);

        // Initialize players with their starting worker positions.
        playerA = new Player("Player A", 0, 0, 1, 1, board);
        playerB = new Player("Player B", 4, 4, 3, 3, board);

        // Player A always starts.
        currentPlayer = playerA;
    }

    // Switches the current player after each turn.
    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerA) ? playerB : playerA;
    }

    // Checks if the current player has won by moving a worker to a level-3 tower.
    private boolean checkVictory() {
        for (int i = 0; i < 2; i++) {
            Worker worker = currentPlayer.getWorker(i);
            int x = worker.getX();
            int y = worker.getY();

            // If a worker is on a level-3 tower, the current player wins.
            if (board.getTowerHeight(x, y) == 3) {
                System.out.println(currentPlayer.getName() + " wins!");
                return true;
            }
        }
        return false;
    }

    /**
     * Processes a turn by moving a worker and building on an adjacent field.
     *
     * This method allows the `currentPlayer` to take their turn by moving one of their workers 
     * and building on an adjacent field. If the move and build actions are valid, it checks for 
     * victory. If the current player wins, the game ends. Otherwise, control switches to the 
     * other player. If the move or build is invalid, an error message prompts the player to retry.
     *
     * @param workerIndex The index of the worker the player intends to move.
     * @param moveX The x-coordinate of the target position for the worker's move.
     * @param moveY The y-coordinate of the target position for the worker's move.
     * @param buildX The x-coordinate of the target position for building.
     * @param buildY The y-coordinate of the target position for building.
     *
     * @pre `workerIndex` corresponds to a valid worker belonging to the `currentPlayer`, and the 
     *      move and build coordinates are within the bounds of the board.
     * @post If the turn is valid, the worker is moved, a block or dome is built, and the game 
     *       checks for victory. If the current player has not won, the turn switches to the 
     *       other player. If the turn is invalid, the player is notified to try again.
     */
    public void playTurn(int workerIndex, int moveX, int moveY, int buildX, int buildY) {
        // The current player takes their turn.
        boolean turnSuccess = currentPlayer.takeTurn(workerIndex, moveX, moveY, buildX, buildY, board);

        if (turnSuccess) {
            // Check if the current player won after the move.
            if (checkVictory()) {
                // End the game if the current player wins.
                return;
            }

            // Switch to the other player if no one has won.
            switchPlayer();
        } else {
            System.out.println("Invalid move or build. Try again.");
        }
    }

    
    /**
     * Displays the current state of the game board in the console.
     *
     * This method outputs a visual representation of the 5x5 board, showing each 
     * field's tower height and occupancy status. For each field:
     * - The tower height (0–3) or dome (4) is shown as an integer.
     * - The occupancy status is represented by "W" if occupied by a worker or "-" 
     *   if unoccupied.
     *
     * Example output:
     * ```
     * 0- 1W 0- 0- 2- 
     * 0- 1- 2W 0- 3W 
     * ...
     * ```
     *
     * @post Outputs the board's current state to the console, with each row printed 
     *       on a new line, and each field represented by its height and occupancy status.
     */
    public void displayBoard() {
        System.out.println("\nCurrent Board:");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int height = board.getTowerHeight(i, j);
                String occupied = board.isOccupied(i, j) ? "W" : "-";
                System.out.print(height + occupied + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Helper method to safely get an integer from the user.
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                scanner.next();  // Clear invalid input
            }
        }
    }

    /**
     * Runs the main game loop for the Santorini game.
     *
     * This method initiates the game, displaying a welcome message and continuously 
     * looping through each player's turn until a winner is determined. In each iteration:
     * - The current board state is displayed.
     * - The current player is prompted to select a worker and specify move and build positions.
     * - The player's turn is processed with `playTurn`, validating the move and building action.
     * - After each turn, the game checks for victory; if a winner is found, the loop breaks.
     *
     * @post The game runs continuously, alternating player turns until a victory is achieved, 
     *       at which point a "Game over!" message is displayed.
     */
    public void run() {
        System.out.println("Welcome to the Santorini game!");

        while (true) {
            // Display the current board state.
            displayBoard();

            // Get input for the current player's turn.
            System.out.println(currentPlayer.getName() + "'s turn:");
            int workerIndex = getIntInput("Select a worker (0 or 1): ");

            int moveX = getIntInput("Enter move position X: ");
            int moveY = getIntInput("Enter move position Y: ");

            int buildX = getIntInput("Enter build position X: ");
            int buildY = getIntInput("Enter build position Y: ");

            // Play the turn.
            playTurn(workerIndex, moveX, moveY, buildX, buildY);

            // Check if there is a winner.
            if (checkVictory()) {
                break;
            }
        }

        System.out.println("Game over!");
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.run();
    }
}
