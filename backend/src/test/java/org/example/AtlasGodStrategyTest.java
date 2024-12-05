// src/test/java/org/example/gods/HermesGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HermesGodStrategyTest {
    private Game game;
    private HermesGodStrategy hermesStrategy;
    private Worker workerA;
    private Worker workerB;

    @BeforeEach
    public void setUp() throws Exception {
        hermesStrategy = new HermesGodStrategy();
        GodStrategy defaultStrategy = new DefaultGodStrategy();
        game = new Game(hermesStrategy, defaultStrategy); // Player A: Hermes, Player B: Default

        // Place workers for Player A and Player B
        game.placeWorker(2, 2); // Player A's first worker
        game.placeWorker(3, 3); // Player B's first worker
        game.placeWorker(2, 3); // Player A's second worker
        game.placeWorker(3, 2); // Player B's second worker

        workerA = game.getPlayerA().getWorkers().get(0); // Player A's first worker
        workerB = game.getPlayerB().getWorkers().get(0); // Player B's first worker
    }

    @Test
    public void testFirstMove() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        boolean moveSuccess = hermesStrategy.move(game, workerA, 2, 3);
        assertTrue(moveSuccess);
        assertEquals(2, workerA.getX());
        assertEquals(3, workerA.getY());

        // Verify move count and original position
        assertEquals(1, hermesStrategy.getStrategyState().get("moveCount"));
        assertEquals(2, hermesStrategy.getStrategyState().get("originalX"));
        assertEquals(2, hermesStrategy.getStrategyState().get("originalY"));
    }

    @Test
    public void testSecondMove() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);
        // Player A performs the second move from (2,3) to (2,4)
        boolean secondMoveSuccess = hermesStrategy.move(game, workerA, 2, 4);
        assertTrue(secondMoveSuccess);
        assertEquals(2, workerA.getX());
        assertEquals(4, workerA.getY());

        // Verify move count reset and original position reset
        assertEquals(0, hermesStrategy.getStrategyState().get("moveCount"));
        assertEquals(-1, hermesStrategy.getStrategyState().get("originalX"));
        assertEquals(-1, hermesStrategy.getStrategyState().get("originalY"));
    }

    @Test
    public void testCannotMoveBackToOriginalPosition() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);

        // Attempt to move back to the original position (2,2)
        Exception exception = assertThrows(Exception.class, () -> {
            hermesStrategy.move(game, workerA, 2, 2);
        });

        String expectedMessage = "Cannot move back to the original position with Hermes's power.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that move count remains at 1
        assertEquals(1, hermesStrategy.getStrategyState().get("moveCount"));
    }

    @Test
    public void testCannotMoveMoreThanTwice() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);
        // Player A performs the second move from (2,3) to (2,4)
        hermesStrategy.move(game, workerA, 2, 4);

        // Attempt to perform a third move
        Exception exception = assertThrows(Exception.class, () -> {
            hermesStrategy.move(game, workerA, 2, 5);
        });

        String expectedMessage = "Cannot move up or down more than one level with Hermes's power.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that move count remains at 0 after two moves
        assertEquals(0, hermesStrategy.getStrategyState().get("moveCount"));
    }

    @Test
    public void testCannotMoveBeyondHeightDifference() throws Exception {
        Board board = game.getBoard();
        // Manually set tower heights to create a height difference
        board.setTowerHeight(2, 2, 1); // Current position
        board.setTowerHeight(2, 3, 3); // Target position (height difference +2)

        // Attempt to move up more than one level
        Exception exception = assertThrows(Exception.class, () -> {
            hermesStrategy.move(game, workerA, 2, 3);
        });

        String expectedMessage = "Cannot move up or down more than one level with Hermes's power.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testNextPhaseAfterTwoMoves() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);
        // Player A performs the second move from (2,3) to (2,4)
        hermesStrategy.move(game, workerA, 2, 4);

        // Transition to next phase
        hermesStrategy.nextPhase(game);

        // Verify that the game has moved to the BUILD phase
        assertEquals(Game.GamePhase.BUILD, game.getCurrentPhase());
    }

    @Test
    public void testNextPhaseAwaitingSecondMove() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);

        // Transition to next phase
        hermesStrategy.nextPhase(game);

        // Verify that the game remains in the MOVE phase, awaiting the second move
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }

    @Test
    public void testPlayerEndsTurn() throws Exception {
        // Player A performs the first move from (2,2) to (2,3)
        hermesStrategy.move(game, workerA, 2, 3);
        // Player A performs the second move from (2,3) to (2,4)
        hermesStrategy.move(game, workerA, 2, 4);

        // Transition to next phase
        hermesStrategy.nextPhase(game);

        // Player A ends turn
        hermesStrategy.playerEndsTurn(game);

        // Verify that Hermes's move state is reset
        assertEquals(0, hermesStrategy.getStrategyState().get("moveCount"));
        assertEquals(-1, hermesStrategy.getStrategyState().get("originalX"));
        assertEquals(-1, hermesStrategy.getStrategyState().get("originalY"));

        // Verify that the current player has switched to Player B
        assertEquals("Player B", game.getCurrentPlayer().getName());
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }
}
