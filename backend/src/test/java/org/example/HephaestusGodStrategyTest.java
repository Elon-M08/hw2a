package org.example.gods;

import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HephaestusGodStrategyTest {

    private Game game;
    private Worker worker;
    private HephaestusGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HephaestusGodStrategy();
        game = new Game(strategy, new DefaultGodStrategy());
        Player player = game.getCurrentPlayer();
        worker = new Worker(player, 1, 1);
        player.addWorker(worker);
        game.getBoard().placeWorker(1, 1, worker);
        game.setCurrentPhase(Game.GamePhase.BUILD);
        game.setSelectedWorker(worker);
    }

    @Test
    void testFirstBuild() throws Exception {
        boolean result = strategy.build(game, worker, 1, 2);
        assertTrue(result);
        assertEquals(1, game.getBoard().getTowerHeight(1, 2));
        assertTrue(strategy.getStrategyState().containsKey("extraBuildAvailable"));
        assertTrue((Boolean) strategy.getStrategyState().get("extraBuildAvailable"));
    }

    @Test
    void testSecondBuildValid() throws Exception {
        strategy.build(game, worker, 1, 2);
        // Second build on the same space
        boolean result = strategy.build(game, worker, 1, 2);
        assertTrue(result);
        assertEquals(2, game.getBoard().getTowerHeight(1, 2));
        assertFalse((Boolean) strategy.getStrategyState().get("extraBuildAvailable"));
    }

    @Test
    void testSecondBuildInvalidDifferentSpace() {
        Exception exception = assertThrows(Exception.class, () -> {
            strategy.build(game, worker, 1, 2);
            // Attempt to build on a different space
            strategy.build(game, worker, 2, 2);
        });
        assertEquals("Second build must be on the same space as the first build.", exception.getMessage());
    }

    @Test
    void testSecondBuildInvalidMaxHeight() throws Exception {
        // Build up to height 3
        game.getBoard().build(1, 2);
        game.getBoard().build(1, 2);
        game.getBoard().build(1, 2);

        boolean result = strategy.build(game, worker, 1, 2);
        assertTrue(result);
        assertEquals(4, game.getBoard().getTowerHeight(1, 2)); // Dome built
        assertFalse((Boolean) strategy.getStrategyState().get("extraBuildAvailable"));
    }
}
