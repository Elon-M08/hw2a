package org.example;

import org.example.gods.DefaultGodStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(new DefaultGodStrategy(), new DefaultGodStrategy());
    }

    @Test
    void testInitialGameState() {
        assertEquals(Game.GamePhase.PLACEMENT, game.getCurrentPhase(), "Initial phase should be PLACEMENT.");
        assertNotNull(game.getBoard(), "Board should be initialized.");
        assertNotNull(game.getPlayerA(), "Player A should be initialized.");
        assertNotNull(game.getPlayerB(), "Player B should be initialized.");
        assertEquals(game.getPlayerA(), game.getCurrentPlayer(), "Player A should be the current player initially.");
    }

    @Test
    void testPlaceWorker() throws Exception {
        // Place all four workers in distinct positions
        assertTrue(game.placeWorker(0, 0), "Player A's first worker should be placed.");
        assertTrue(game.placeWorker(1, 0), "Player A's second worker should be placed.");
        assertTrue(game.placeWorker(0, 1), "Player B's first worker should be placed.");
        assertTrue(game.placeWorker(1, 1), "Player B's second worker should be placed.");

        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase(), "Phase should switch to MOVE after all workers are placed.");
    }


    @Test
    void testInvalidMoveWorker() throws Exception {
        // Place enough workers to enter MOVE phase:
        game.placeWorker(0, 0); // A1
        game.placeWorker(1, 0); // A2
        game.placeWorker(0, 1); // B1
        game.placeWorker(1, 1); // B2

        // MOVE phase now
        assertThrows(Exception.class, () -> game.moveWorker(0, -1, -1), "Moving out of bounds should throw an exception.");
        assertThrows(Exception.class, () -> game.moveWorker(0, 5, 5), "Moving out of bounds should throw an exception.");
        
        // Attempting to move to the same position (0,0) again:
        assertThrows(Exception.class, () -> game.moveWorker(0, 0, 0), "Moving to the same position should throw an exception.");
    }

    @Test
    void testBuild() throws Exception {
        // Place all workers
        game.placeWorker(0, 0); // A1
        game.placeWorker(2, 0); // A2
        game.placeWorker(0, 1); // B1
        game.placeWorker(2, 1); // B2

        // Now in MOVE phase
        // Move A1 from (0,0) to (1,0) to set selectedWorker
        assertTrue(game.moveWorker(0, 1, 0), "Move should succeed.");

        // After a successful move, the default strategy likely sets the next phase to BUILD
        // Now we can build adjacent to (1,0), for example (1,1) if it's free
        boolean result = game.build(1, 1);
        assertTrue(result, "Build action should be successful.");
        assertEquals(1, game.getBoard().getTowerHeight(1, 1), "Tower height at (1, 1) should be 1 after building.");
    }

    @Test
    void testInvalidBuild() throws Exception {
        // Place all workers
        game.placeWorker(0, 0); // A1
        game.placeWorker(2, 0); // A2
        game.placeWorker(0, 1); // B1
        game.placeWorker(2, 1); // B2

        // Move a worker first to enter BUILD phase
        assertTrue(game.moveWorker(0, 1, 0), "Move should succeed.");

        // Out of bounds builds
        assertThrows(Exception.class, () -> game.build(-1, -1), "Building out of bounds should throw an exception.");
        assertThrows(Exception.class, () -> game.build(5, 5), "Building out of bounds should throw an exception.");

        // Building on a worker's position (0,0) occupied by A1 originally (now A1 moved)
        // Let's try building on (2,0) where A2 is placed:
        assertThrows(Exception.class, () -> game.build(2, 0), "Building on a worker's position should throw an exception.");
    }

    @Test
    void testSwitchPlayer() throws Exception {
        assertEquals(game.getPlayerA(), game.getCurrentPlayer(), "Initial player should be Player A.");
        game.switchPlayer();
        assertEquals(game.getPlayerB(), game.getCurrentPlayer(), "Player should switch to Player B.");
        game.switchPlayer();
        assertEquals(game.getPlayerA(), game.getCurrentPlayer(), "Player should switch back to Player A.");
    }

 

}
