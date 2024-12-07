package org.example;

import org.example.gods.GodStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {

    private Player player;
    private GodStrategy mockGodStrategy;

    @BeforeEach
    void setUp() {
        mockGodStrategy = mock(GodStrategy.class); // Mock the GodStrategy to isolate Player
        player = new Player("TestPlayer", mockGodStrategy);
    }

    @Test
    void testConstructorInitializesFields() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(mockGodStrategy, player.getGodStrategy());
        assertTrue(player.getWorkers().isEmpty());
    }

    @Test
    void testAddWorkerSuccessfully() {
        Worker worker1 = new Worker(player, 0, 0);
        Worker worker2 = new Worker(player, 1, 1);

        player.addWorker(worker1);
        player.addWorker(worker2);

        assertEquals(2, player.getWorkers().size());
        assertTrue(player.getWorkers().contains(worker1));
        assertTrue(player.getWorkers().contains(worker2));
    }

    @Test
    void testAddWorkerThrowsExceptionWhenExceedingLimit() {
        Worker worker1 = new Worker(player, 0, 0);
        Worker worker2 = new Worker(player, 1, 1);
        Worker worker3 = new Worker(player, 2, 2);

        player.addWorker(worker1);
        player.addWorker(worker2);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> player.addWorker(worker3));
        assertEquals("Player already has two workers.", exception.getMessage());
    }

    @Test
    void testGetWorkerByValidIndex() {
        Worker worker1 = new Worker(player, 0, 0);
        Worker worker2 = new Worker(player, 1, 1);

        player.addWorker(worker1);
        player.addWorker(worker2);

        assertEquals(worker1, player.getWorker(0));
        assertEquals(worker2, player.getWorker(1));
    }

    @Test
    void testGetWorkerByInvalidIndexReturnsNull() {
        Worker worker1 = new Worker(player, 0, 0);
        player.addWorker(worker1);

        assertNull(player.getWorker(-1));
        assertNull(player.getWorker(2));
    }

    @Test
    void testGetWorkersReturnsImmutableList() {
        Worker worker1 = new Worker(player, 0, 0);
        player.addWorker(worker1);

        var workers = player.getWorkers();
        assertThrows(UnsupportedOperationException.class, () -> workers.add(new Worker(player, 1, 1)));
    }

    @Test
    void testAddWorkerMaintainsOrder() {
        Worker worker1 = new Worker(player, 0, 0);
        Worker worker2 = new Worker(player, 1, 1);

        player.addWorker(worker1);
        player.addWorker(worker2);

        assertEquals(worker1, player.getWorkers().get(0));
        assertEquals(worker2, player.getWorkers().get(1));
    }
}
