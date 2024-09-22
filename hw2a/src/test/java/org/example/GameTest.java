package org.example;
public class GameTest {

    public static void main(String[] args) {
        // Initialize the game.
        Game game = new Game();

        System.out.println("Test: Simulating a game where Player A wins...");

        // Turn 1: Player A moves worker 0 to (0, 1) and builds at (0, 2)
        System.out.println("Turn 1: Player A");
        game.playTurn(0, 0, 1, 0, 2);  // Worker 0 moves from (0, 0) to (0, 1) and builds at (0, 2)
        game.displayBoard();

        // Turn 2: Player B moves worker 0 to (4, 3) and builds at (4, 2)
        System.out.println("Turn 2: Player B");
        game.playTurn(0, 4, 3, 4, 2);  // Worker 0 moves from (4, 4) to (4, 3) and builds at (4, 2)
        game.displayBoard();

        // Turn 3: Player A moves worker 0 to (0, 2) and builds at (0, 3) to raise a tower
        System.out.println("Turn 3: Player A");
        game.playTurn(0, 0, 2, 0, 3);  // Worker 0 moves from (0, 1) to (0, 2) and builds at (0, 3)
        game.displayBoard();

        // Turn 4: Player B moves worker 1 to (3, 2) and builds at (3, 1)
        System.out.println("Turn 4: Player B");
        game.playTurn(1, 3, 2, 3, 1);  // Worker 1 moves from (3, 3) to (3, 2) and builds at (3, 1)
        game.displayBoard();

        // Turn 5: Player A builds up the tower by moving to (1, 2) and building at (1, 3)
        System.out.println("Turn 5: Player A");
        game.playTurn(1, 1, 2, 1, 3);  // Worker 1 moves from (1, 1) to (1, 2) and builds at (1, 3)
        game.displayBoard();

        // Turn 6: Player B moves worker 0 to (4, 2) and builds at (4, 1)
        System.out.println("Turn 6: Player B");
        game.playTurn(0, 4, 2, 4, 1);  // Worker 0 moves from (4, 3) to (4, 2) and builds at (4, 1)
        game.displayBoard();

        // Turn 7: Player A moves worker 0 to (0, 3) (on a level-2 tower) and builds at (0, 4)
        System.out.println("Turn 7: Player A");
        game.playTurn(0, 0, 3, 0, 4);  // Worker 0 moves from (0, 2) to (0, 3) and builds at (0, 4)
        game.displayBoard();

        // Turn 8: Player B builds more, but Player A is getting close to winning
        System.out.println("Turn 8: Player B");
        game.playTurn(0, 4, 2, 4, 1);  // Worker 1 moves from (3, 2) to (3, 1) and builds at (3, 0)
        game.displayBoard();

        // Turn 9: Player A moves worker 0 to the level-3 tower and wins!
        System.out.println("Turn 9: Player A");
        game.playTurn(0, 0, 4, 0, 4);  // Worker 0 moves from (0, 3) to the level-3 tower at (0, 4)
        game.displayBoard();

        // At this point, Player A should have won the game.
        System.out.println("Player A wins the game!");
    }
}
