package org.example;

public class PlayerTest {

    public static void main(String[] args) {
        testPlayerInitialization();
        testGetWorker();
        testTakeTurn();
        System.out.println("All Player unit tests passed!");
    }

    // Test the initialization of the Player and their workers
    public static void testPlayerInitialization() {
        // Create a board
        Board board = new Board();

        // Initialize a player with two workers
        Player player = new Player("Player 1", 0, 0, 1, 1, board);

        // Check if player's name is correctly set
        assert player.getName().equals("Player 1") : "Player name should be 'Player 1'";

        // Check if the two workers are placed on the board at the correct positions
        Worker worker1 = player.getWorker(0);
        Worker worker2 = player.getWorker(1);

        assert worker1 != null : "Worker 1 should be initialized";
        assert worker2 != null : "Worker 2 should be initialized";

    }

    // Test the getWorker method
    public static void testGetWorker() {
        // Create a board
        Board board = new Board();

        // Initialize a player with two workers
        Player player = new Player("Player 1", 0, 0, 1, 1, board);

        // Retrieve the workers using getWorker
        Worker worker1 = player.getWorker(0);
        Worker worker2 = player.getWorker(1);

        // Ensure correct workers are returned
        assert worker1 != null : "Worker 1 should not be null";
        assert worker2 != null : "Worker 2 should not be null";
        assert player.getWorker(2) == null : "Index out of bounds should return null";
    }

    // Test the takeTurn method
    public static void testTakeTurn() {
        // Create a board
        Board board = new Board();

        // Initialize a player with two workers
        Player player = new Player("Player 1", 0, 0, 1, 1, board);

        // Move Worker 1 from (0, 0) to (0, 1) and build at (1, 1)
        boolean turnResult = player.takeTurn(0, 0, 1, 1, 1, board);
        assert turnResult : "Player should have successfully moved and built a block";

        // Check that worker moved to (0, 1)
        Worker worker1 = player.getWorker(0);
        assert worker1.getX() == 0 && worker1.getY() == 1 : "Worker 1 should have moved to (0, 1)";

        // Check that a block was built at (1, 1)
        assert board.getTowerHeight(1, 1) == 1 : "Block should have been built at (1, 1)";

        // Test a failed move (e.g., moving worker to an occupied tile)
        boolean failedTurn = player.takeTurn(0, 0, 1, 1, 1, board);  // Invalid move
        assert !failedTurn : "Move should fail if worker tries to move to an invalid position";
    }
}
