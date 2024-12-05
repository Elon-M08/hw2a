package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinotaurGodStrategyTest {

    private Game game;
    private Worker minotaurWorker;
    private Worker opponentWorker;
    private MinotaurGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MinotaurGodStrategy();
        game = new Game(strategy, new DefaultGodStrategy());
        Player player = game.getCurrentPlayer();
        minotaurWorker = new Worker(player, 2, 2);
        player.addWorker(minotaurWorker);
        game.getBoard().placeWorker(2, 2, minotaurWorker);

        game.switchPlayer();
        Player opponent = game.getCurrentPlayer();
        opponentWorker = new Worker(opponent, 3, 3);
        opponent.addWorker(opponentWorker);
        game.getBoard().placeWorker(3, 3, opponentWorker);

        game.switchPlayer(); // Switch back to Minotaur's turn
    }

    @Test
    void testMoveIntoOccupiedSpace() throws Exception {
        boolean result = strategy.move(game, minotaurWorker, 3, 3);
        assertTrue(result);
        // Minotaur is now at (3,3), opponent worker is pushed to (4,4)
        assertEquals(3, minotaurWorker.getX());
        assertEquals(3, minotaurWorker.getY());
        assertEquals(4, opponentWorker.getX());
        assertEquals(4, opponentWorker.getY());
    }

    @Test
    void testMoveIntoEdgeOccupiedSpace() {
        Exception exception = assertThrows(Exception.class, () -> {
            opponentWorker.setPosition(4, 4);
            game.getBoard().moveWorker(3, 3, 4, 4);
            strategy.move(game, minotaurWorker, 4, 4);
        });
        assertEquals("Cannot push opponent's worker off the board.", exception.getMessage());
    }
}
