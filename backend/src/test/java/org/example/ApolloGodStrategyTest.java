// src/test/java/org/example/gods/ApolloGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApolloGodStrategyTest {
    private Game game;
    private ApolloGodStrategy apolloStrategy;
    private Worker workerA;
    private Worker workerB;

    @BeforeEach
    public void setUp() throws Exception {
        apolloStrategy = new ApolloGodStrategy();
        GodStrategy defaultStrategy = new DefaultGodStrategy();
        game = new Game(apolloStrategy, defaultStrategy); // Player A: Apollo, Player B: Default

        // Place workers for Player A and Player B
        game.placeWorker(2, 2); // Player A's first worker
        game.placeWorker(3, 3); // Player B's first worker
        game.placeWorker(2, 3); // Player A's second worker
        game.placeWorker(3, 2); // Player B's second worker

        workerA = game.getPlayerA().getWorkers().get(0); // Player A's first worker
        workerB = game.getPlayerB().getWorkers().get(0); // Player B's first worker
    }

    @Test
    public void testStandardMove() throws Exception {
        // Attempt a standard move to an empty cell
        boolean moveSuccess = apolloStrategy.move(game, workerA, 2, 4);
        assertTrue(moveSuccess);
        assertEquals(2, workerA.getX());
        assertEquals(4, workerA.getY());
        assertFalse((Boolean) apolloStrategy.getStrategyState().getOrDefault("hasSwapped", false));
    }

    @Test
    public void testSwapWithOpponent() throws Exception {
        // Attempt to swap with opponent's worker at (3,3)
        boolean swapSuccess = apolloStrategy.move(game, workerA, 3, 3);
        assertTrue(swapSuccess);

        // Verify that workerA is now at (3,3) and workerB is at (2,2)
        assertEquals(3, workerA.getX());
        assertEquals(3, workerA.getY());
        assertEquals(2, workerB.getX());
        assertEquals(2, workerB.getY());

        // Verify that swap state is updated
        assertTrue((Boolean) apolloStrategy.getStrategyState().get("hasSwapped"));
    }

    @Test
    public void testSwapWithOwnWorker() throws Exception {
        // Attempt to swap with own worker at (2,3)
        Worker ownWorker = game.getPlayerA().getWorkers().get(1); // Player A's second worker
        Exception exception = assertThrows(Exception.class, () -> {
            apolloStrategy.move(game, workerA, 2, 3);
        });

        String expectedMessage = "Cannot move into a cell occupied by your own worker.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that positions remain unchanged
        assertEquals(2, workerA.getX());
        assertEquals(2, workerA.getY());
        assertEquals(2, ownWorker.getX());
        assertEquals(3, ownWorker.getY());
    }

    @Test
    public void testSwapWhenOriginalPositionOccupied() throws Exception {
        // Occupy the original position (2,2) with another worker
        Worker additionalWorker = new Worker(game.getPlayerA(), 2, 2);
        boolean placeSuccess = game.getBoard().placeWorker(2, 2, additionalWorker);
        game.getPlayerA().addWorker(additionalWorker);
        assertTrue(placeSuccess);

        // Attempt to swap with opponent's worker at (3,3)
        Exception exception = assertThrows(Exception.class, () -> {
            apolloStrategy.move(game, workerA, 3, 3);
        });

        String expectedMessage = "Cannot swap because the original position is occupied.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that positions remain unchanged
        assertEquals(2, workerA.getX());
        assertEquals(2, workerA.getY());
        assertEquals(3, workerB.getX());
        assertEquals(3, workerB.getY());
    }

    @Test
    public void testBuildAfterMove() throws Exception {
        // Move workerA to (2,4)
        boolean moveSuccess = apolloStrategy.move(game, workerA, 2, 4);
        assertTrue(moveSuccess);

        // Proceed to build phase
        apolloStrategy.nextPhase(game);
        game.setCurrentPhase(Game.GamePhase.BUILD);

        // Attempt to build at (2,5)
        boolean buildSuccess = apolloStrategy.build(game, workerA, 2, 5);
        assertTrue(buildSuccess);
        assertEquals(1, game.getBoard().getTowerHeight(2, 5));
    }
}
