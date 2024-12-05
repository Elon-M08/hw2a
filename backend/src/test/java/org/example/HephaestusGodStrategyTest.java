// src/test/java/org/example/gods/HephaestusGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HephaestusGodStrategyTest {
    private Game game;
    private HephaestusGodStrategy hephaestusStrategy;
    private Worker workerA;
    private Worker workerB;

    @BeforeEach
    public void setUp() throws Exception {
        hephaestusStrategy = new HephaestusGodStrategy();
        GodStrategy defaultStrategy = new DefaultGodStrategy();
        game = new Game(hephaestusStrategy, defaultStrategy); // Player A: Hephaestus, Player B: Default

        // Place workers for Player A and Player B
        game.placeWorker(2, 2); // Player A's first worker
        game.placeWorker(3, 3); // Player B's first worker
        game.placeWorker(2, 3); // Player A's second worker
        game.placeWorker(3, 2); // Player B's second worker

        workerA = game.getPlayerA().getWorkers().get(0); // Player A's first worker
        workerB = game.getPlayerB().getWorkers().get(0); // Player B's first worker
    }

    @Test
    public void testStandardBuild() throws Exception {
        // Player A performs a standard build at (2,3)
        boolean buildSuccess = hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue(buildSuccess);
        assertEquals(1, game.getBoard().getTowerHeight(2, 3));

        // Verify that hasPerformedExtraBuild is true and build coordinates are recorded
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));
        assertEquals(2, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(3, hephaestusStrategy.getStrategyState().get("firstBuildY"));
    }

    @Test
    public void testExtraBuildSameCell() throws Exception {
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));

        // Player A performs the extra build at (2,3)
        boolean extraBuildSuccess = hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue(extraBuildSuccess);
        assertEquals(2, game.getBoard().getTowerHeight(2, 3));

        // Verify that hasPerformedExtraBuild is reset
        assertFalse((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));
        assertEquals(-1, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(-1, hephaestusStrategy.getStrategyState().get("firstBuildY"));
    }

    @Test
    public void testExtraBuildDifferentCell() throws Exception {
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));

        // Attempt to perform the extra build at a different cell (2,4)
        Exception exception = assertThrows(Exception.class, () -> {
            hephaestusStrategy.build(game, workerA, 2, 4);
        });

        String expectedMessage = "Extra build must be on the same cell as the first build.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that hasPerformedExtraBuild remains true
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));
        assertEquals(2, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(3, hephaestusStrategy.getStrategyState().get("firstBuildY"));
    }

    @Test
    public void testCannotBuildBeyondHeight4() throws Exception {
        Board board = game.getBoard();
        // Manually set tower height to 4 at (2,3)
        board.setTowerHeight(2, 3, 4);

        // Player A attempts to perform the first build at (2,3)
        Exception exception = assertThrows(Exception.class, () -> {
            hephaestusStrategy.build(game, workerA, 2, 3);
        });

        String expectedMessage = "Cannot build beyond height 4.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testCannotBuildExtraOnDome() throws Exception {
        Board board = game.getBoard();
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));
        assertEquals(2, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(3, hephaestusStrategy.getStrategyState().get("firstBuildY"));

        // Manually set tower height to 4 at (2,3) to simulate a dome
        board.setTowerHeight(2, 3, 4);

        // Attempt to perform the extra build at (2,3)
        Exception exception = assertThrows(Exception.class, () -> {
            hephaestusStrategy.build(game, workerA, 2, 3);
        });

        String expectedMessage = "Cannot perform extra build on a cell that already has a dome.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that hasPerformedExtraBuild remains true
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));
        assertEquals(2, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(3, hephaestusStrategy.getStrategyState().get("firstBuildY"));
    }

    @Test
    public void testNextPhaseAfterFirstBuild() throws Exception {
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);
        assertTrue((Boolean) hephaestusStrategy.getStrategyState().get("hasPerformedExtraBuild"));

        // Transition to next phase
        hephaestusStrategy.nextPhase(game);

        // Verify that the game is still in the BUILD phase, awaiting extra build
        assertEquals(Game.GamePhase.BUILD, game.getCurrentPhase());
    }

    @Test
    public void testNextPhaseAfterExtraBuild() throws Exception {
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);
        // Player A performs the extra build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);

        // Transition to next phase
        hephaestusStrategy.nextPhase(game);

        // Verify that the game has moved to the MOVE phase
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }

    @Test
    public void testPlayerEndsTurn() throws Exception {
        // Player A performs the first build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);

        // Transition to next phase
        hephaestusStrategy.nextPhase(game);

        // Player A performs the extra build at (2,3)
        hephaestusStrategy.build(game, workerA, 2, 3);

        // Player A ends turn
        hephaestusStrategy.playerEndsTurn(game);

        // Verify that Hephaestus's build state is reset
        assertFalse((Boolean) hephaestusStrategy.getStrategyState().getOrDefault("hasPerformedExtraBuild", false));
        assertEquals(-1, hephaestusStrategy.getStrategyState().get("firstBuildX"));
        assertEquals(-1, hephaestusStrategy.getStrategyState().get("firstBuildY"));

        // Verify that the current player has switched to Player B
        assertEquals("Player B", game.getCurrentPlayer().getName());
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }
}
