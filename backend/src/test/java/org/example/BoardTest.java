package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testIsOccupied() {
        Worker worker = new Worker(null, 0, 0); // No owner for simplicity
        board.placeWorker(0, 0, worker);
        assertTrue(board.isOccupied(0, 0), "Cell (0,0) should be occupied after placing a worker.");
        assertFalse(board.isOccupied(1, 1), "Cell (1,1) should not be occupied.");
    }

    @Test
    void testGetTowerHeight() {
        board.build(0, 0);
        assertEquals(1, board.getTowerHeight(0, 0), "Height should be 1 after one build.");
    }

    @Test
    void testSetTowerHeight() {
        board.setTowerHeight(0, 0, 3);
        assertEquals(3, board.getTowerHeight(0, 0), "Tower height should be set to 3.");
    }

    @Test
    void testIsAdjacent() {
        assertTrue(board.isAdjacent(1, 1, 1, 2), "Cells (1,1) and (1,2) should be adjacent.");
        assertFalse(board.isAdjacent(0, 0, 2, 2), "Cells (0,0) and (2,2) should not be adjacent.");
    }

    @Test
    void testBuild() {
        assertTrue(board.build(0, 0), "Build should be successful at (0,0).");
        assertEquals(1, board.getTowerHeight(0, 0), "Tower height should be 1 after one build.");
        board.build(0, 0); // Build again
        assertEquals(2, board.getTowerHeight(0, 0), "Tower height should be 2 after two builds.");
    }

    @Test
    void testPlaceWorker() {
        Worker worker = new Worker(null, 0, 0); // No owner for simplicity
        assertTrue(board.placeWorker(0, 0, worker), "Placing a worker at (0,0) should succeed.");
        assertEquals(worker, board.getWorkerAt(0, 0), "Worker at (0,0) should match the placed worker.");
    }

    @Test
    void testMoveWorker() {
        Worker worker = new Worker(null, 0, 0); // No owner for simplicity
        board.placeWorker(0, 0, worker);
        assertTrue(board.moveWorker(0, 0, 0, 1), "Moving worker from (0,0) to (0,1) should succeed.");
        assertEquals(worker, board.getWorkerAt(0, 1), "Worker should be at (0,1) after move.");
        assertNull(board.getWorkerAt(0, 0), "Cell (0,0) should be empty after move.");
    }

    @Test
    void testSwapWorkers() {
        Worker worker1 = new Worker(null, 0, 0);
        Worker worker2 = new Worker(null, 1, 1);
        board.placeWorker(0, 0, worker1);
        board.placeWorker(1, 1, worker2);

        assertTrue(board.swapWorkers(worker1, worker2), "Swapping workers at (0,0) and (1,1) should succeed.");
        assertEquals(worker1, board.getWorkerAt(1, 1), "Worker1 should now be at (1,1).");
        assertEquals(worker2, board.getWorkerAt(0, 0), "Worker2 should now be at (0,0).");
    }

    @Test
    void testResetBoard() {
        board.build(0, 0);
        Worker worker = new Worker(null, 0, 0);
        board.placeWorker(0, 0, worker);
        board.resetBoard();

        assertEquals(0, board.getTowerHeight(0, 0), "Tower height should be 0 after reset.");
        assertNull(board.getWorkerAt(0, 0), "Cell (0,0) should be empty after reset.");
    }

    @Test
    void testIsWithinBounds() {
        assertTrue(board.isWithinBounds(0, 0), "(0,0) should be within bounds.");
        assertFalse(board.isWithinBounds(5, 5), "(5,5) should be out of bounds.");
    }

    @Test
    void testGetGrid() {
        int[][] grid = board.getGrid();
        assertEquals(0, grid[0][0], "Initial grid value should be 0.");
        board.build(0, 0);
        grid = board.getGrid();
        assertEquals(1, grid[0][0], "Grid value should be updated after building.");
    }

    @Test
    void testEquals() {
        Board otherBoard = new Board();
        assertEquals(board, otherBoard, "Two new boards should be equal.");
        board.build(0, 0);
        assertNotEquals(board, otherBoard, "Boards should not be equal after modifying one.");
    }
}
