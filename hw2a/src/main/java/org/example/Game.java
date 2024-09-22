package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    private Player playerA;
    private Player playerB;
    private Board board;
    private Player currentPlayer;
    private Scanner scanner;

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

    // Process a turn: move a worker and build on an adjacent field.
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

    // Display the current state of the board (optional: for debugging).
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

    // Run the game loop.
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
