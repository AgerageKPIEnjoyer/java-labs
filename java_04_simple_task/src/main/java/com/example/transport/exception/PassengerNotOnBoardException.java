package com.example.transport.exception;

/**
 * Thrown when trying to disembark a passenger who is not currently on the vehicle.
 */
public class PassengerNotOnBoardException extends RuntimeException {
    public PassengerNotOnBoardException(String message) {
        super(message);
    }
}
