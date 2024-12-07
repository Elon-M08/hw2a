package org.example.gods;

import org.example.Board;
import org.example.Game;
import org.example.Player;
import org.example.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List; // Import for List
import java.util.Map;  // Import for Map

import static org.junit.jupiter.api.Assertions.*;

class HephaestusGodStrategyTest {

    private Game game;
    private Player playerA;
    private Worker hephaestusWorker;
    private HephaestusGodStrategy hephaestusStrategy;

    @BeforeEach
    void setUp() throws Exception {
        hephaestusStrategy = new HephaestusGodStrategy();
        playerA = new Player("Player A", hephaestusStrategy);
        game = new Game(hephaestusStrategy, new DefaultGodStrategy());

        // Place Hephaestus's worker at (2,2)
        hephaestusWorker = new Worker(playerA, 2, 2);
        game.getBoard().placeWorker(2, 2, hephaestusWorker);
        playerA.addWorker(hephaestusWorker);
    }

    @Test
    void testSingleBuildSuccess() throws Exception {
        // Perform the first build at (2,3)
        boolean buildSuccess = hephaestusStrategy.build(game, hephaestusWorker, 2, 3);

        // Validate the build succeeded
        assertTrue(buildSuccess, "The first build should succeed.");
        assertEquals(1, game.getBoard().getTowerHeight(2, 3), "Tower height should be 1 after the first build.");

        // Verify the state for the extra build
        assertTrue(hephaestusStrategy.getStrategyState().get("extraBuildAvailable").equals(true), "Extra build should be available.");
        assertEquals(2, hephaestusStrategy.getStrategyState().get("firstBuildX"), "First build X-coordinate should be 2.");
        assertEquals(3, hephaestusStrategy.getStrategyState().get("firstBuildY"), "First build Y-coordinate should be 3.");
    }

    @Test
    void testExtraBuildSuccess() throws Exception {
        // Perform the first build at (2,3)
        hephaestusStrategy.build(game, hephaestusWorker, 2, 3);

        // Perform the second build on the same cell
        boolean extraBuildSuccess = hephaestusStrategy.build(game, hephaestusWorker, 2, 3);

        // Validate the second build succeeded
        assertTrue(extraBuildSuccess, "The second build should succeed.");
        assertEquals(2, game.getBoard().getTowerHeight(2, 3), "Tower height should be 2 after the second build.");

        // Verify the state reset after the extra build
        assertFalse(hephaestusStrategy.getStrategyState().get("extraBuildAvailable").equals(true), "Extra build should no longer be available.");
    }

    @Test
    void testExtraBuildOnDifferentCellFails() throws Exception {
        // Perform the first build at (2,3)
        hephaestusStrategy.build(game, hephaestusWorker, 2, 3);

        // Attempt the second build on a different cell
        Exception exception = assertThrows(Exception.class, () -> {
            hephaestusStrategy.build(game, hephaestusWorker, 3, 3);
        });

        String expectedMessage = "Extra build must be on the same cell as the first build.";
        assertEquals(expectedMessage, exception.getMessage(), "The extra build should fail if not on the same cell.");
    }


    @Test
    void testBuildOnOccupiedCellFails() {
        // Place another worker at (2,3)
        Worker blockingWorker = new Worker(playerA, 2, 3);
        game.getBoard().placeWorker(2, 3, blockingWorker);

        // Attempt to build at the occupied cell
        Exception exception = assertThrows(Exception.class, () -> {
            hephaestusStrategy.build(game, hephaestusWorker, 2, 3);
        });

        String expectedMessage = "Cannot build on an occupied space.";
        assertEquals(expectedMessage, exception.getMessage(), "Building on an occupied cell should fail.");
    }

    @Test
    void testSelectableBuildCellsDuringExtraBuild() throws Exception {
        // Perform the first build at (2,3)
        hephaestusStrategy.build(game, hephaestusWorker, 2, 3);

        // Get selectable build cells during the extra build phase
        List<Map<String, Integer>> selectableCells = hephaestusStrategy.getSelectableBuildCells(game, hephaestusWorker);

        // Validate only the first build cell is selectable
        assertEquals(1, selectableCells.size(), "Only one cell should be selectable during the extra build phase.");
        assertEquals(2, (int) selectableCells.get(0).get("x"), "Selectable cell X-coordinate should be 2.");
        assertEquals(3, (int) selectableCells.get(0).get("y"), "Selectable cell Y-coordinate should be 3.");
    }
}
