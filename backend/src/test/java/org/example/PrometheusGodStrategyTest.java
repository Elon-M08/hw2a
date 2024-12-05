// src/test/java/org/example/gods/PrometheusGodStrategyTest.java
package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrometheusGodStrategyTest {

    private Game game;
    private Worker worker;
    private PrometheusGodStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PrometheusGodStrategy();
        game = new Game(strategy, new DefaultGodStrategy());
        Player player = game.getCurrentPlayer();
        worker = new Worker(player, 2, 2);
        player.addWorker(worker);
        game.getBoard().placeWorker(2, 2, worker);
    }

    @Test
    void testBuildBeforeMoveAndCannotMoveUp() throws Exception {
        Board board = game.getBoard();

        // Player builds before moving
        boolean buildSuccess = strategy.build(game, worker, 2, 3); // Build at (2,3)
        assertTrue(buildSuccess);
        assertTrue((Boolean) strategy.getStrategyState().get("hasBuiltBeforeMove"));

        // Attempt to move up after building
        board.build(2, 2); // Current height increases to 1
        board.build(2, 3); // Target height increases to 1

        // Move from (2,2) at height 1 to (2,3) at height 1 - valid move (same level)
        boolean moveSuccess = strategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);
        assertEquals(2, worker.getX());
        assertEquals(3, worker.getY());

        // Attempt to move up to height 2 after building before moving
        board.build(2, 4); // Target cell height increases to 2

        Exception exception = assertThrows(Exception.class, () -> {
            strategy.move(game, worker, 2, 4);
        });

        String expectedMessage = "Cannot move up after building before moving.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Verify that the worker has not moved to (2,4)
        assertEquals(2, worker.getX());
        assertEquals(3, worker.getY());
    }

    @Test
    void testBuildBeforeMoveAndMoveSameLevel() throws Exception {
        Board board = game.getBoard();

        // Player builds before moving
        boolean buildSuccess = strategy.build(game, worker, 2, 3); // Build at (2,3)
        assertTrue(buildSuccess);
        assertTrue((Boolean) strategy.getStrategyState().get("hasBuiltBeforeMove"));

        // Move to the same level
        board.build(2, 2); // Current height increases to 1
        board.build(2, 3); // Target height increases to 1

        boolean moveSuccess = strategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);
        assertEquals(2, worker.getX());
        assertEquals(3, worker.getY());

        // Verify that victory is checked correctly (not triggered)
        boolean victory = strategy.checkVictory(game, worker);
        assertFalse(victory);
        assertFalse(game.isGameEnded());
    }

    @Test
    void testMoveWithoutBuildAndCanMoveUp() throws Exception {
        Board board = game.getBoard();

        // Player does not build before moving

        // Set current height to 1
        board.build(2, 2); // Height 1
        worker.setPosition(2, 2);

        // Set target cell to height 2
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2

        // Move up
        boolean moveSuccess = strategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);
        assertEquals(2, worker.getX());
        assertEquals(3, worker.getY());

        // Check victory if moved to level 3
        board.build(2, 3); // Height 3
        worker.setPosition(2, 3); // Move worker to height 3

        boolean victory = strategy.checkVictory(game, worker);
        assertTrue(victory);
        assertTrue(game.isGameEnded());
    }

    @Test
    void testCannotBuildAfterMoving() throws Exception {
        Board board = game.getBoard();

        // Player moves before building
        board.build(2, 2); // Height 1
        board.build(2, 3); // Height 1
        boolean moveSuccess = strategy.move(game, worker, 2, 3);
        assertTrue(moveSuccess);
        assertEquals(2, worker.getX());
        assertEquals(3, worker.getY());

        // Attempt to build before moving (which is now build after move phase)
        Exception exception = assertThrows(Exception.class, () -> {
            strategy.build(game, worker, 2, 4);
        });

        String expectedMessage = "Cannot build at this phase.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCannotBuildMultipleTimesBeforeMove() throws Exception {
        Board board = game.getBoard();

        // Player builds before moving
        boolean firstBuild = strategy.build(game, worker, 2, 3); // Build at (2,3)
        assertTrue(firstBuild);
        assertTrue((Boolean) strategy.getStrategyState().get("hasBuiltBeforeMove"));

        // Attempt to build again before moving
        Exception exception = assertThrows(Exception.class, () -> {
            strategy.build(game, worker, 2, 4); // Another build before moving
        });

        String expectedMessage = "You have already built before moving.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testNextPhaseAfterBuildBeforeMove() throws Exception {
        Board board = game.getBoard();

        // Player builds before moving
        strategy.build(game, worker, 2, 3); // Build at (2,3)

        // Transition to next phase
        strategy.nextPhase(game);

        // Verify that the phase is set to BUILD_AFTER_MOVE
        assertEquals(Game.GamePhase.BUILD_AFTER_MOVE, game.getCurrentPhase());
    }

    @Test
    void testNextPhaseAfterMove() throws Exception {
        Board board = game.getBoard();

        // Player moves without building
        board.build(2, 2); // Height 1
        board.build(2, 3); // Height 1
        strategy.move(game, worker, 2, 3); // Move to (2,3)

        // Transition to next phase
        strategy.nextPhase(game);

        // Verify that the phase is set to BUILD
        assertEquals(Game.GamePhase.BUILD, game.getCurrentPhase());
    }

    @Test
    void testPlayerEndsTurnAfterBuildBeforeMove() throws Exception {
        Board board = game.getBoard();

        // Player builds before moving
        strategy.build(game, worker, 2, 3); // Build at (2,3)

        // Transition to next phase
        strategy.nextPhase(game); // Now in BUILD_AFTER_MOVE phase

        // Player ends turn
        strategy.playerEndsTurn(game);

        // Verify that the state is reset
        assertFalse((Boolean) strategy.getStrategyState().get("hasBuiltBeforeMove"));

        // Verify that the current player has switched to Player B
        assertEquals("Player B", game.getCurrentPlayer().getName());
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }

    @Test
    void testPlayerEndsTurnAfterMoveWithoutBuild() throws Exception {
        Board board = game.getBoard();

        // Player moves without building
        board.build(2, 2); // Height 1
        board.build(2, 3); // Height 1
        strategy.move(game, worker, 2, 3); // Move to (2,3)

        // Transition to next phase
        strategy.nextPhase(game); // Now in BUILD phase

        // Player ends turn
        strategy.playerEndsTurn(game);

        // Verify that the state is reset
        assertFalse((Boolean) strategy.getStrategyState().get("hasBuiltBeforeMove"));

        // Verify that the current player has switched to Player B
        assertEquals("Player B", game.getCurrentPlayer().getName());
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase());
    }

    @Test
    void testCannotMoveUpWithoutBuildingBeforeMove() throws Exception {
        Board board = game.getBoard();

        // Player moves without building
        board.build(2, 2); // Height 1
        board.build(2, 3); // Height 1
        board.build(2, 3); // Height 2
        strategy.move(game, worker, 2, 3); // Move to (2,3) at height 2

        // Attempt to move up to height 3
        board.build(2, 4); // Height 3
        boolean moveSuccess = strategy.move(game, worker, 2, 4);
        assertTrue(moveSuccess);
        assertEquals(2, worker.getX());
        assertEquals(4, worker.getY());

        // Check victory if moved to level 3
        board.build(2, 4); // Height 3
        worker.setPosition(2, 4); // Move worker to height 3

        boolean victory = strategy.checkVictory(game, worker);
        assertTrue(victory);
        assertTrue(game.isGameEnded());
    }
}
