package org.example;

/**
 * Custom exception thrown when an invalid placement action is attempted,
 * such as placing a worker on an occupied or out-of-bounds position.
 */
public class InvalidPlacementException extends Exception {
    /**
     * Constructor to create an exception with a specific message.
     *
     * @param message The detail message.
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}
