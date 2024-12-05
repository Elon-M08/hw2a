package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultGodStrategyTest {

    private Game game;
    private Worker worker;
    private DefaultGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new DefaultGodStrategy();
        game = new Game(strategy, strategy);
        Player player = game.getCurrentPlayer();
        worker = new Worker(player, 0, 0);
        player.addWorker(worker);
        game.getBoard().placeWorker(0, 0, worker);
    }

    @Test
    void testMoveValid() throws Exception {
        boolean result = strategy.move(game, worker, 0, 1);
        assertTrue(result);
        assertEquals(0, worker.getX());
        assertEquals(1, worker.getY());
    }

    @Test
    void testMoveInvalid() throws Exception {
        game.getBoard().build(0, 1); // Increase height to make move invalid
        game.getBoard().build(0, 1);
        game.getBoard().build(0, 1);
        boolean result = strategy.move(game, worker, 0, 1);
        assertFalse(result);
    }

    @Test
    void testBuildValid() throws Exception {
        boolean result = strategy.build(game, worker, 1, 0);
        assertTrue(result);
        assertEquals(1, game.getBoard().getTowerHeight(1, 0));
    }

    @Test
    void testBuildInvalid() throws Exception {
        game.getBoard().build(1, 0);
        game.getBoard().build(1, 0);
        game.getBoard().build(1, 0);
        game.getBoard().build(1, 0); // Max height
        boolean result = strategy.build(game, worker, 1, 0);
        assertFalse(result);
    }

    @Test
    void testCheckVictory() throws Exception {
        game.getBoard().build(0, 0);
        game.getBoard().build(0, 0);
        game.getBoard().build(0, 0); // Height is now 3
        worker.setPosition(0, 0); // Move worker to (0,0)
        boolean result = strategy.checkVictory(game, worker);
        assertTrue(result);
    }
}
