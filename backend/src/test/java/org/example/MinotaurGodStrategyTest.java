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
    private Player playerA;
    private Player playerB;
    private Worker minotaurWorker;
    private Worker opponentWorker;
    private MinotaurGodStrategy minotaurStrategy;

    @BeforeEach
    void setUp() throws Exception {
        minotaurStrategy = new MinotaurGodStrategy();
        playerA = new Player("Player A", minotaurStrategy);
        playerB = new Player("Player B", new DefaultGodStrategy());
        game = new Game(minotaurStrategy, new DefaultGodStrategy());

        // Place Minotaur's worker at (2,2)
        minotaurWorker = new Worker(playerA, 2, 2);
        game.getBoard().placeWorker(2, 2, minotaurWorker);
        playerA.addWorker(minotaurWorker);

        // Place opponent's worker at (2,3)
        opponentWorker = new Worker(playerB, 2, 3);
        game.getBoard().placeWorker(2, 3, opponentWorker);
        playerB.addWorker(opponentWorker);
    }

    @Test
    void testMinotaurPushesOpponentWorker() throws Exception {
        // Minotaur moves into the cell (2,3) occupied by the opponent's worker
        boolean moveResult = minotaurStrategy.move(game, minotaurWorker, 2, 3);

        // Verify the move succeeded
        assertTrue(moveResult, "Minotaur should successfully move and push the opponent's worker.");

        // Verify opponent's worker has been pushed to (2,4)
        Worker pushedWorker = game.getBoard().getWorkerAt(2, 4);
        assertNotNull(pushedWorker, "Opponent's worker should have been pushed to (2,4).");
        assertEquals(opponentWorker, pushedWorker, "Pushed worker should be the opponent's worker.");

        // Verify Minotaur's worker is now at (2,3)
        Worker movedWorker = game.getBoard().getWorkerAt(2, 3);
        assertNotNull(movedWorker, "Minotaur's worker should have moved to (2,3).");
        assertEquals(minotaurWorker, movedWorker, "Moved worker should be Minotaur's worker.");
    }


    @Test
    void testMinotaurCannotPushIntoOccupiedSpace() {
        // Place another worker at (2,4) to block the push
        Worker blockingWorker = new Worker(playerB, 2, 4);
        game.getBoard().placeWorker(2, 4, blockingWorker);

        // Attempt Minotaur's move
        Exception exception = assertThrows(Exception.class, () -> {
            minotaurStrategy.move(game, minotaurWorker, 2, 3);
        });

        // Validate the error message
        String expectedMessage = "Invalid move: Cannot push opponent's worker into an occupied space.";
        assertEquals(expectedMessage, exception.getMessage(), "Should not allow pushing into an occupied space.");
    }

    @Test
    void testMinotaurCannotMoveIntoOwnWorkerSpace() {
        // Place another worker for Player A at (3,2)
        Worker ownWorker = new Worker(playerA, 3, 2);
        game.getBoard().placeWorker(3, 2, ownWorker);

        // Attempt Minotaur's move
        Exception exception = assertThrows(Exception.class, () -> {
            minotaurStrategy.move(game, minotaurWorker, 3, 2);
        });

        // Validate the error message
        String expectedMessage = "Invalid move: Cannot move into your own worker's space.";
        assertEquals(expectedMessage, exception.getMessage(), "Should not allow moving into own worker's space.");
    }
}
