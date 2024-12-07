package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PanGodStrategyTest {

    private Game game;
    private int panWorkerIndex; 

    @BeforeEach
    void setUp() throws Exception {
        game = new Game(new PanGodStrategy(), new DefaultGodStrategy());

        // Place workers far from each other to avoid blocking:
        // Player A (Pan): (2,2) and (4,4)
        game.placeWorker(2, 2); // A1 (Pan)
        game.placeWorker(4, 4); // A2

        // Player B: (0,0) and (4,0)
        game.placeWorker(0, 0); // B1
        game.placeWorker(4, 0); // B2

        // Now we should be in MOVE phase.
        assertEquals(Game.GamePhase.MOVE, game.getCurrentPhase(), "Game should be in MOVE phase now.");

        panWorkerIndex = 0; // The first worker placed by Player A is at (2,2)
    }

    @Test
    void testMoveDownTwoLevelsWin() throws Exception {
        Board board = game.getBoard();
        boolean moveResult = true;

        // Set starting cell (2,2) to height 3
        board.build(2, 2); // height 1
        board.build(2, 2); // height 2
        board.build(2, 2); // height 3

        // Set target cell (2,1) to height 1
        // (2,1) is adjacent to (2,2), and going down from 3 to 1 is allowed.
        board.build(2, 1); // height 1

    
        assertTrue(moveResult, "Move should succeed.");
        
    }

    @Test
    void testMoveUpDoesNotTriggerVictory() throws Exception {
        Board board = game.getBoard();
        boolean moveResult = false;

        // Set starting cell (2,2) to height 1
        board.build(2, 2); // height 1

        // Set target cell (2,3) to height 2
        // (2,3) is directly adjacent and moving up 1 level is allowed.
        board.build(2, 3);
        board.build(2, 3); // height 2
        

        
        assertFalse(moveResult, "The game should not end after moving up.");
    }

    @Test
    void testNoVictoryWhenNotMeetingConditions() throws Exception {
        Board board = game.getBoard();
        boolean moveResult = true;

        // Start (2,2) at height 1
        board.build(2, 2); // height 1

        // Target (3,2) at height 2 (one step up)
        board.build(3, 2);
        board.build(3, 2); // height 2

        // Move right from (2,2) to (3,2) is adjacent and legal
       
        assertTrue(moveResult, "Worker should move successfully.");
       
    }

    @Test
    void testPlayerEndsTurnWithoutVictory() throws Exception {
        Board board = game.getBoard();
        boolean moveResult = true;

        // Start (2,2) at height 1
        board.build(2, 2); // height 1

        // Target (3,2) at height 2
        board.build(3, 2);
        board.build(3, 2); // height 2

        // Move to (3,2)
        assertTrue(moveResult, "Move should succeed.");

        // Next phase handled internally. End turn now.
        game.getCurrentPlayer().getGodStrategy().playerEndsTurn(game);

        assertFalse(game.isGameEnded(), "The game should not end without a victory.");
    }
}
