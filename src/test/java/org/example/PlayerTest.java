package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Board board;
    private Player player;

    @BeforeEach
    public void setUp() {
        board = new Board();
        player = new Player("Player A", 0, 0, 1, 1, board);
    }

    // Test player initialization
    @Test
    public void testPlayerInitialization() {
        assertEquals("Player A", player.getName(), "Player name should be initialized correctly");

        Worker worker1 = player.getWorker(0);
        Worker worker2 = player.getWorker(1);

        assertNotNull(worker1, "Worker 1 should be initialized");
        assertNotNull(worker2, "Worker 2 should be initialized");

        // Verify worker positions on the board
        assertTrue(board.isOccupied(0, 0), "Worker 1 should be placed at (0, 0)");
        assertTrue(board.isOccupied(1, 1), "Worker 2 should be placed at (1, 1)");
    }

    // Test retrieving workers by index
    @Test
    public void testGetWorker() {
        assertNotNull(player.getWorker(0), "Worker at index 0 should exist");
        assertNotNull(player.getWorker(1), "Worker at index 1 should exist");

        // Test invalid index
        assertNull(player.getWorker(-1), "Invalid index should return null");
        assertNull(player.getWorker(2), "Invalid index should return null");
    }

    // Test a valid turn with move and build
    @Test
    public void testTakeTurnValidMoveAndBuild() {
        // Assume worker 0 is at (0, 0), attempt to move to (0, 1) and build at (0, 2)
        boolean result = player.takeTurn(0, 0, 1, 0, 2, board);

        assertTrue(result, "Turn should be successful with valid move and build");
        assertTrue(board.isOccupied(0, 1), "Worker should have moved to (0, 1)");
        assertEquals(1, board.getTowerHeight(0, 2), "Build action should increase tower height to 1 at (0, 2)");
    }

    // Test an invalid turn with an invalid move
    @Test
    public void testTakeTurnInvalidMove() {
        // Attempt to move worker at (0, 0) to an invalid position (e.g., out of bounds)
        boolean result = player.takeTurn(0, 5, 5, 0, 2, board);

        assertFalse(result, "Turn should fail due to invalid move");
        assertTrue(board.isOccupied(0, 0), "Worker should not have moved from the original position");
        assertEquals(0, board.getTowerHeight(0, 2), "Build action should not occur on invalid turn");
    }

    // Test an invalid turn with a build on an occupied field
    @Test
    public void testTakeTurnInvalidBuildLocation() {
        // Move worker 0 to (0, 1) and try to build on occupied field (0, 1)
        player.takeTurn(0, 0, 1, 0, 1, board);

        boolean result = player.takeTurn(1, 1, 0, 0, 1, board);

        assertFalse(result, "Turn should fail due to build on an occupied field");
        assertEquals(0, board.getTowerHeight(1, 1), "Build action should not increase tower height on occupied field");
    }
}
