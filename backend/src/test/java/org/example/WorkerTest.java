package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkerTest {

    private Player mockPlayer;
    private Worker worker;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        worker = new Worker(mockPlayer, 1, 2);
    }

    @Test
    void testConstructorInitializesFields() {
        assertEquals(mockPlayer, worker.getOwner());
        assertEquals(1, worker.getX());
        assertEquals(2, worker.getY());
    }

    @Test
    void testGetIdIsUnique() {
        Worker worker2 = new Worker(mockPlayer, 3, 4);
        assertNotEquals(worker.getId(), worker2.getId());
    }

    @Test
    void testSetPositionUpdatesCoordinates() {
        worker.setPosition(3, 4);
        assertEquals(3, worker.getX());
        assertEquals(4, worker.getY());
    }

    @Test
    void testGetPositionReturnsCorrectPosition() {
        Position position = worker.getPosition();
        assertEquals(1, position.getX());
        assertEquals(2, position.getY());
    }

    @Test
    void testToStringReturnsCorrectFormat() {
        String expected = "Worker[Owner=TestPlayer, Position=(1, 2)]";
        assertEquals(expected, worker.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Worker worker2 = new Worker(mockPlayer, 1, 2);
        Worker worker3 = new Worker(mockPlayer, 3, 4);

        assertTrue(worker.equals(worker2));
        assertFalse(worker.equals(worker3));
        assertEquals(worker.hashCode(), worker2.hashCode());
        assertNotEquals(worker.hashCode(), worker3.hashCode());
    }

    @Test
    void testEqualsWithDifferentOwner() {
        Player anotherPlayer = mock(Player.class);
        when(anotherPlayer.getName()).thenReturn("AnotherPlayer");

        Worker worker2 = new Worker(anotherPlayer, 1, 2);

        assertFalse(worker.equals(worker2));
    }
}
