package org.example;import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;
    private Player playerA;
    private Worker worker;

    @BeforeEach
    public void setUp() {
        board = new Board();
        playerA = new Player("Player A", 0, 0, 1, 1, board);
        worker = new Worker(playerA, 0, 0);
    }



    @Test
    public void testBuildingOnEmptyField() {
        assertTrue(board.build(2, 2), "Building on an empty field should be allowed");
        assertEquals(1, board.getTowerHeight(2, 2), "Tower height should increase to 1 after one build");

        board.build(2, 2);
        assertEquals(2, board.getTowerHeight(2, 2), "Tower height should increase to 2 after two builds");

        board.build(2, 2);
        assertEquals(3, board.getTowerHeight(2, 2), "Tower height should increase to 3 after three builds");

        board.build(2, 2);
        assertEquals(4, board.getTowerHeight(2, 2), "Tower height should be set to 4 (dome) after four builds");
        assertTrue(board.isOccupied(2, 2), "Field should be occupied by a dome after reaching height 4");
    }

    @Test
    public void testBuildingOnOccupiedField() {
        board.placeWorker(1, 1, worker);
        assertFalse(board.build(1, 1), "Building on an occupied field should not be allowed");
        assertEquals(0, board.getTowerHeight(1, 1), "Tower height should remain 0 on an occupied field");
    }

    @Test
    public void testPlaceWorkerOnEmptyField() {
        board.placeWorker(1, 1, worker);
        assertTrue(board.isOccupied(1, 1), "Field should be occupied after placing a worker");
    }

    @Test
    public void testMoveWorkerValid() {
        board.placeWorker(2, 2, worker);
        board.moveWorker(2, 2, 3, 3);

        assertTrue(board.isOccupied(3, 3), "Worker should be at the new position after a valid move");
        assertFalse(board.isOccupied(2, 2), "Original position should be empty after the move");
    }

    @Test
    public void testMoveWorkerInvalid() {
        board.placeWorker(2, 2, worker);
        board.build(3, 3);
        board.build(3, 3);  // Build twice to make height difference > 1

        board.moveWorker(2, 2, 3, 3);

        assertTrue(board.isOccupied(2, 2), "Worker should remain at the original position after an invalid move");
        assertFalse(board.isOccupied(3, 3), "Worker should not move to an invalid position");
    }
}
