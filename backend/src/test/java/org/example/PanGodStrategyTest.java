package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PanGodStrategyTest {

    private Game game;
    private Worker worker;
    private PanGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PanGodStrategy();
        game = new Game(strategy, new DefaultGodStrategy());
        Player player = game.getCurrentPlayer();
        worker = new Worker(player, 2, 2);
        player.addWorker(worker);
        game.getBoard().placeWorker(2, 2, worker);
    }

    @Test
    void testMoveDownTwoLevelsWin() throws Exception {
        game.getBoard().build(2, 2); // Height 1
        game.getBoard().build(2, 2); // Height 2
        game.getBoard().build(2, 2); // Height 3
        worker.setPosition(2, 2); // Place worker on height 3

        game.getBoard().build(2, 3); // Height 1
        game.getBoard().build(2, 3); // Height 2
        worker.setPosition(2, 3); // Move worker to height 2

        boolean result = strategy.move(game, worker, 2, 4); // Move down to height 0
        assertTrue(result);
        assertTrue(strategy.checkVictory(game, worker));
        assertTrue(game.isGameEnded());
    }

    @Test
    void testNormalVictoryCondition() throws Exception {
        game.getBoard().build(2, 2); // Height 1
        game.getBoard().build(2, 2); // Height 2
        game.getBoard().build(2, 2); // Height 3
        worker.setPosition(2, 2); // Move worker to height 3
        boolean result = strategy.checkVictory(game, worker);
        assertTrue(result);
        assertTrue(game.isGameEnded());
    }
}
