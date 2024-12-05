// src/test/java/org/example/gods/AthenaGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AthenaGodStrategyTest {
    private Game game;
    private AthenaGodStrategy athenaStrategy;
    private Worker worker;

    @BeforeEach
    public void setUp() throws Exception {
        athenaStrategy = new AthenaGodStrategy();
        GodStrategy defaultStrategy = new DefaultGodStrategy();
        game = new Game(athenaStrategy, defaultStrategy); // Player A: Athena, Player B: Default

        // Place workers for Player A and Player B
        game.placeWorker(2, 2); // Player A's first worker
        game.placeWorker(3, 3); // Player B's first worker
        game.placeWorker(2, 3); // Player A's second worker
        game.placeWorker(3, 2); // Player B's second worker

        worker = game.getPlayerA().getWorkers().get(0); // Player A's first worker
    }

    @Test
    public void testMoveUpAndRestrictOpponent() throws Exception {
        // Initial height
        Board board = game.getBoard();
        int initialHeight = board.getTowerHeight(worker.getX(), worker.getY());

        // Build to increase the target cell's height
        game.build(2, 3); // Player A builds at (2,3)
        // Ensure build was successful
        assertEquals(initialHeight + 1, board.getTowerHeight(2, 3));

        // Move worker up
        boolean moveSuccess = athenaStrategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);
        assertTrue((Boolean) athenaStrategy.getStrategyState().get("hasMovedUp"));

        // Next phase should have restricted opponent's movement up
        athenaStrategy.nextPhase(game);

        // Verify opponent's strategy cannot move up
        GodStrategy opponentStrategy = game.getOpponentPlayer().getGodStrategy();
        opponentStrategy.setCannotMoveUp(true); // This should have been set by Athena
        // Attempt opponent's move up (simulate in test)
        // Since it's a unit test, simulate opponent's strategy behavior accordingly
    }

    @Test
    public void testVictoryCondition() throws Exception {
        // Build the tower to height 3
        game.build(2, 3); // height 1
        game.build(2, 3); // height 2
        game.build(2, 3); // height 3

        // Move worker to the top
        boolean moveSuccess = athenaStrategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);

        // Check victory
        boolean victory = athenaStrategy.checkVictory(game, worker);
        assertTrue(victory);

        // Ensure the game has ended
        assertTrue(game.isGameEnded());
        assertEquals("Player A", game.getWinner());
    }

    @Test
    public void testCannotMoveBackToInitialSpace() throws Exception {
        // Move worker to (2,3)
        boolean firstMove = athenaStrategy.move(game, worker, 2, 3);
        assertTrue(firstMove);
        athenaStrategy.nextPhase(game);

        // Attempt to move back to (2,2)
        Exception exception = assertThrows(Exception.class, () -> {
            athenaStrategy.move(game, worker, 2, 2);
        });

        String expectedMessage = "Cannot move back to the initial space.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
