package com.example.transport.vehicle;

import com.example.transport.person.FireFighter;

/**
 * A kind of Car. Can only carry FireFighter passengers.
 */
public class FireTruck extends Car<FireFighter> {
    public FireTruck(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}
