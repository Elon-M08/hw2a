package org.example;

import org.example.gods.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameIntegrationTest {

    private Game game;

    @BeforeEach
    void setUp() {
        // Initialize game with two different gods
        GodStrategy playerAStrategy = new DemeterGodStrategy();
        GodStrategy playerBStrategy = new HephaestusGodStrategy();
        game = new Game(playerAStrategy, playerBStrategy);

        // Place workers for both players
        try {
            game.placeWorker(0, 0); // Player A
            game.placeWorker(4, 4); // Player B
            game.placeWorker(0, 1); // Player A
            game.placeWorker(4, 3); // Player B
        } catch (Exception e) {
            fail("Failed to place workers: " + e.getMessage());
        }
    }

    @Test
    void testDemeterExtraBuild() throws Exception {
        game.setCurrentPhase(Game.GamePhase.MOVE);
        game.setSelectedWorker(game.getCurrentPlayer().getWorker(0));

        // Move
        boolean moveResult = game.moveWorker(0, 1, 1);
        assertTrue(moveResult);

        // Build first time
        game.setCurrentPhase(Game.GamePhase.BUILD);
        boolean buildResult1 = game.build(1, 2);
        assertTrue(buildResult1);
        assertEquals(1, game.getBoard().getTowerHeight(1, 2));

        // Build second time
        boolean buildResult2 = game.build(2, 2);
        assertTrue(buildResult2);
        assertEquals(1, game.getBoard().getTowerHeight(2, 2));

        // End turn
        game.getCurrentPlayer().getGodStrategy().nextPhase(game);
        assertEquals("Player B", game.getCurrentPlayer().getName());
    }

    @Test
    void testHephaestusDoubleBuild() throws Exception {
        // Switch to Player B's turn
        game.switchPlayer();
        game.setCurrentPhase(Game.GamePhase.MOVE);
        game.setSelectedWorker(game.getCurrentPlayer().getWorker(0));

        // Move
        boolean moveResult = game.moveWorker(0, 3, 3);
        assertTrue(moveResult);

        // Build first time
        game.setCurrentPhase(Game.GamePhase.BUILD);
        boolean buildResult1 = game.build(3, 2);
        assertTrue(buildResult1);
        assertEquals(1, game.getBoard().getTowerHeight(3, 2));

        // Build second time on same space
        boolean buildResult2 = game.build(3, 2);
        assertTrue(buildResult2);
        assertEquals(2, game.getBoard().getTowerHeight(3, 2));

        // End turn
        game.getCurrentPlayer().getGodStrategy().nextPhase(game);
        assertEquals("Player A", game.getCurrentPlayer().getName());
    }

    @Test
    void testGameEndsWhenPlayerWins() throws Exception {
        // Simulate a win for Player A using Pan's ability
        game = new Game(new PanGodStrategy(), new DefaultGodStrategy());
        Player playerA = game.getCurrentPlayer();
        Worker worker = new Worker(playerA, 2, 2);
        playerA.addWorker(worker);
        game.getBoard().placeWorker(2, 2, worker);

        // Build tower of height 2 at (2,2)
        game.getBoard().build(2, 2);
        game.getBoard().build(2, 2);

        // Move worker to height 2
        worker.setPosition(2, 2);

        // Move down two levels
        game.setCurrentPhase(Game.GamePhase.MOVE);
        game.setSelectedWorker(worker);
        boolean moveResult = game.moveWorker(0, 2, 3);
        assertTrue(moveResult);

        // Check victory
        assertTrue(game.isGameEnded());
        assertEquals("Player A", game.getCurrentPlayer().getName());
    }
}
