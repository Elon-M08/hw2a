package org.example;import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;
    private Board board;
    private Player playerA;
    private Player playerB;

    @BeforeEach
    public void setUp() {
        game = new Game();
        board = new Board();
        playerA = new Player("Player A", 0, 0, 1, 1, board);
        playerB = new Player("Player B", 4, 4, 3, 3, board);
    }

    @Test
    public void testGameInitialization() {
        assertNotNull(game);
        assertEquals("Player A", game.getCurrentPlayer().getName(), "Player A should start the game");
    }
    
    @Test
    public void testPlayTurnWithInvalidMove() {
        game.getCurrentPlayer().getWorker(0).move(0, 0, board);
        game.playTurn(0, 3, 3, 1, 1);
    
        assertTrue(board.isOccupied(0, 0), "Worker should not have moved from the original position");
        assertEquals(0, board.getTowerHeight(1, 1), "Tower height should remain unchanged after invalid move");
    }
    
    @Test
    public void testPlayTurnWithInvalidBuildLocation() {
        game.getCurrentPlayer().getWorker(0).move(0, 0, board);
        board.placeWorker(1, 1, game.getCurrentPlayer().getWorker(1));
    
        game.playTurn(0, 0, 1, 0, 1);
    
        assertEquals(0, board.getTowerHeight(1, 1), "Build should not be allowed on an occupied field");
    }
    

    @Test
    public void testDisplayBoard() {
        assertDoesNotThrow(() -> game.displayBoard(), "displayBoard should run without errors");
    }
}
