package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testIsOccupied() {
        Board board = new Board();
        assertFalse(board.isOccupied(2, 2));
        board.build(2, 2);
        assertFalse(board.isOccupied(2, 2)); // Still not occupied by a worker or dome
        board.build(2, 2);
        board.build(2, 2);
        board.build(2, 2); // Now height is 4 (dome)
        assertTrue(board.isOccupied(2, 2)); // Occupied by dome
    }

    @Test
    void testPlaceWorker() {
        Board board = new Board();
        Worker worker = new Worker(null, 1, 1);
        boolean result = board.placeWorker(1, 1, worker);
        assertTrue(result);
        assertEquals(worker, board.getWorkerAt(1, 1));
    }

    @Test
    void testMoveWorker() {
        Board board = new Board();
        Worker worker = new Worker(null, 0, 0);
        board.placeWorker(0, 0, worker);
        boolean result = board.moveWorker(0, 0, 0, 1);
        assertTrue(result);
        assertNull(board.getWorkerAt(0, 0));
        assertEquals(worker, board.getWorkerAt(0, 1));
    }
}
