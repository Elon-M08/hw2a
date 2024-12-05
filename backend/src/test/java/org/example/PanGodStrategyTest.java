// src/test/java/org/example/gods/PanGodStrategyTest.java
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
        Board board = game.getBoard();

        // Build up the starting cell to height 3
        board.build(2, 2); // Height 1
        board.build(2, 2); // Height 2
        board.build(2, 2); // Height 3
        worker.setPosition(2, 2); // Place worker on height 3

        // Set the target cell to height 2 (moving down one level)
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2
        worker.setPosition(2, 3); // Move worker to height 2

        // Perform the move down two levels to height 0
        boolean result = strategy.move(game, worker, 2, 1); // Assuming (2,1) is at height 0
        assertTrue(result);
        assertTrue(strategy.checkVictory(game, worker));
        assertTrue(game.isGameEnded());
    }

    @Test
    void testMoveDownThreeLevelsWin() throws Exception {
        Board board = game.getBoard();

        // Build up the starting cell to height 3
        board.build(2, 2); // Height 1
        board.build(2, 2); // Height 2
        board.build(2, 2); // Height 3
        worker.setPosition(2, 2); // Place worker on height 3

        // Set the target cell to height 0 (moving down three levels)
        // Assuming (2,1) is at height 0
        worker.setPosition(2, 1); // Move worker to height 0

        boolean result = strategy.move(game, worker, 2, 1);
        assertTrue(result);
        assertTrue(strategy.checkVictory(game, worker));
        assertTrue(game.isGameEnded());
    }

    @Test
    void testNormalVictoryCondition() throws Exception {
        Board board = game.getBoard();

        // Build up the starting cell to height 3
        board.build(2, 2); // Height 1
        board.build(2, 2); // Height 2
        board.build(2, 2); // Height 3
        worker.setPosition(2, 2); // Move worker to height 3

        boolean result = strategy.checkVictory(game, worker);
        assertTrue(result);
        assertTrue(game.isGameEnded());
    }

    @Test
    void testMoveUpDoesNotTriggerVictory() throws Exception {
        Board board = game.getBoard();

        // Set starting position to height 1
        board.build(2, 2); // Height 1
        worker.setPosition(2, 2);

        // Move up one level to height 2
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2
        boolean result = strategy.move(game, worker, 2, 3);
        assertTrue(result);
        assertFalse(strategy.checkVictory(game, worker));
        assertFalse(game.isGameEnded());
    }

    @Test
    void testNoVictoryWhenNotMeetingConditions() throws Exception {
        Board board = game.getBoard();

        // Set starting position to height 1
        board.build(2, 2); // Height 1
        worker.setPosition(2, 2);

        // Move to height 2
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2
        boolean result = strategy.move(game, worker, 2, 3);
        assertTrue(result);
        assertFalse(strategy.checkVictory(game, worker));
        assertFalse(game.isGameEnded());
    }

    @Test
    void testPlayerEndsTurnWithoutVictory() throws Exception {
        Board board = game.getBoard();

        // Set starting position to height 1
        board.build(2, 2); // Height 1
        worker.setPosition(2, 2);

        // Move to height 2
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2
        strategy.move(game, worker, 2, 3);

        // Transition to next phase
        strategy.nextPhase(game);

        // Player ends turn
        strategy.playerEndsTurn(game);

        // Verify that the game is not ended
        assertFalse(game.isGameEnded());

        // Verify that the current player has switched
        assertEquals("Player B", game.getCurrentPlayer().getName());
    }
}
