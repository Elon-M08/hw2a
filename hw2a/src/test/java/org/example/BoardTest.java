package org.example;

public class BoardTest {
    public static void main(String[] args) {
        testBuildBlock();
        System.out.println("All unit tests passed!");
    }

    public static void testBuildBlock() {
        // Create a board
        Board board = new Board();

        // Build a block at (0, 0)
        boolean result = board.build(0, 0);
        assert result : "Block should have been built at (0, 0)";
        assert board.getTowerHeight(0, 0) == 1 : "Tower level at (0, 0) should be 1";

        // Build another block at (0, 0)
        board.build(0, 0);
        assert board.getTowerHeight(0, 0) == 2 : "Tower level at (0, 0) should be 2";

        // Build until level 3
        board.build(0, 0);
        assert board.getTowerHeight(0, 0) == 3 : "Tower level at (0, 0) should be 3";

        // Try to build on a level 3 block (should fail since a dome needs to be built)
        result = board.build(0, 0);
        assert !result : "No more blocks should be built on a level 3 block";
    }
}
