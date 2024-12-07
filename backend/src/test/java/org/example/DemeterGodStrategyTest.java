package org.example.gods;

import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DemeterGodStrategyTest {

    private Game game;
    private Worker worker;
    private DemeterGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new DemeterGodStrategy();
        game = new Game(strategy, new DefaultGodStrategy());
        Player player = game.getCurrentPlayer();
        worker = new Worker(player, 2, 2);
        player.addWorker(worker);
        game.getBoard().placeWorker(2, 2, worker);
        game.setCurrentPhase(Game.GamePhase.BUILD);
        game.setSelectedWorker(worker);
    }

    @Test
    void testFirstBuild() throws Exception {
        boolean result = strategy.build(game, worker, 2, 3);
        assertTrue(result);
        assertEquals(1, game.getBoard().getTowerHeight(2, 3));
        assertTrue(strategy.getStrategyState().containsKey("extraBuildAvailable"));
        assertTrue((Boolean) strategy.getStrategyState().get("extraBuildAvailable"));
    }

    @Test
    void testSecondBuildValid() throws Exception {
        strategy.build(game, worker, 2, 3);
        // Attempt to build on a different space
        boolean result = strategy.build(game, worker, 3, 3);
        assertTrue(result);
        assertEquals(1, game.getBoard().getTowerHeight(3, 3));
        assertFalse((Boolean) strategy.getStrategyState().get("extraBuildAvailable"));
    }

}
