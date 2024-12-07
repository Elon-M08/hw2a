package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testConstructorInitializesCoordinates() {
        Position position = new Position(3, 5);

        assertEquals(3, position.getX());
        assertEquals(5, position.getY());
    }

    @Test
    void testToStringReturnsCorrectFormat() {
        Position position = new Position(2, 4);

        String expected = "Position(X=2, Y=4)";
        assertEquals(expected, position.toString());
    }

    @Test
    void testEqualsReturnsTrueForSameCoordinates() {
        Position position1 = new Position(1, 1);
        Position position2 = new Position(1, 1);

        assertTrue(position1.equals(position2));
        assertTrue(position2.equals(position1));
    }

    @Test
    void testEqualsReturnsFalseForDifferentCoordinates() {
        Position position1 = new Position(1, 1);
        Position position2 = new Position(2, 2);

        assertFalse(position1.equals(position2));
        assertFalse(position2.equals(position1));
    }

    @Test
    void testEqualsReturnsFalseForDifferentObjectTypes() {
        Position position = new Position(1, 1);

        assertFalse(position.equals("NotAPosition"));
        assertFalse(position.equals(null));
    }

    @Test
    void testHashCodeIsConsistentForEqualObjects() {
        Position position1 = new Position(1, 1);
        Position position2 = new Position(1, 1);

        assertEquals(position1.hashCode(), position2.hashCode());
    }

    @Test
    void testHashCodeDiffersForDifferentCoordinates() {
        Position position1 = new Position(1, 1);
        Position position2 = new Position(2, 2);

        assertNotEquals(position1.hashCode(), position2.hashCode());
    }
}
