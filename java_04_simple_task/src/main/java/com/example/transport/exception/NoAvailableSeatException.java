package com.example.transport.exception;

/**
 * Thrown when a vehicle has no free seats left for a boarding passenger.
 */
public class NoAvailableSeatException extends RuntimeException {
    public NoAvailableSeatException(String message) {
        super(message);
    }
}
