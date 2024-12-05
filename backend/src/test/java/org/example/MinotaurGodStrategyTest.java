// src/test/java/org/example/gods/MinotaurGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class MinotaurGodStrategyTest {

    private Game game;
    private Player playerA;
    private Player playerB;
    private Worker workerA;
    private Worker workerB;
    private MinotaurGodStrategy minotaurStrategy;

    @BeforeEach
    void setUp() throws Exception {
        minotaurStrategy = new MinotaurGodStrategy();
        playerA = new Player("Player A", minotaurStrategy);
        playerB = new Player("Player B", new DefaultGodStrategy());
        game = new Game(minotaurStrategy, new DefaultGodStrategy());

        // Place Player A's worker at (2,2)
        workerA = new Worker(playerA, 2, 2);
        game.getBoard().placeWorker(2, 2, workerA);
        playerA.addWorker(workerA);

        // Place Player B's worker at (2,3)
        workerB = new Worker(playerB, 2, 3);
        game.getBoard().placeWorker(2, 3, workerB);
        playerB.addWorker(workerB);
    }

    @Test
    void testMoveIntoOpponentCellSuccessfullyPushes() throws Exception {
        // Player A's worker attempts to move to (2,3) where Player B's worker is
        boolean moveResult = minotaurStrategy.move(game, workerA, 2, 3);
        assertTrue(moveResult, "Minotaur should successfully move into opponent's cell.");

        // Check that Player B's worker has been pushed to (2,4)
        Worker pushedWorker = game.getBoard().getWorkerAt(2, 4);
        assertNotNull(pushedWorker, "Opponent's worker should have been pushed to (2,4).");
        assertEquals(workerB, pushedWorker, "Pushed worker should be Player B's worker.");

        // Check that Player A's worker is now at (2,3)
        Worker movedWorker = game.getBoard().getWorkerAt(2, 3);
        assertNotNull(movedWorker, "Minotaur's worker should have moved to (2,3).");
        assertEquals(workerA, movedWorker, "Moved worker should be Player A's worker.");
    }

    @Test
    void testMoveIntoOpponentCellPushesOutOfBounds() {
        // Attempt to move Player A's worker from (2,2) to (2,3), pushing Player B's worker to (2,4)
        // Now, place Player B's worker at (2,4) which is the edge; pushing further would be out of bounds
        game.getBoard().buildTower(2, 4, 3); // Assuming tower height < 4
        game.getBoard().buildTower(2, 4, 3); // Now at height 3

        // Place Player B's worker at (2,4)
        Worker workerC = new Worker(playerB, 2, 4);
        game.getBoard().placeWorker(2, 4, workerC);
        playerB.addWorker(workerC);

        Exception exception = assertThrows(Exception.class, () -> {
            minotaurStrategy.move(game, workerA, 2, 3);
        });

        String expectedMessage = "Invalid move: Cannot push opponent's worker out of bounds.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Should throw exception for pushing out of bounds.");
    }

    @Test
    void testMoveIntoOpponentCellPushesIntoOccupiedSpace() {
        // Place another worker at (2,4) to make pushing into (2,4) invalid
        Worker workerC = new Worker(playerB, 2, 4);
        game.getBoard().placeWorker(2, 4, workerC);
        playerB.addWorker(workerC);

        Exception exception = assertThrows(Exception.class, () -> {
            minotaurStrategy.move(game, workerA, 2, 3);
        });

        String expectedMessage = "Invalid move: Cannot push opponent's worker into an occupied space.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Should throw exception for pushing into occupied space.");
    }

    @Test
    void testMoveIntoOwnWorkerCellFails() {
        // Place Player A's second worker at (3,2)
        Worker workerA2 = new Worker(playerA, 3, 2);
        game.getBoard().placeWorker(3, 2, workerA2);
        playerA.addWorker(workerA2);

        Exception exception = assertThrows(Exception.class, () -> {
            minotaurStrategy.move(game, workerA, 3, 2);
        });

        String expectedMessage = "Invalid move: Cannot move into your own worker's space.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Should throw exception for moving into own worker's space.");
    }
}
