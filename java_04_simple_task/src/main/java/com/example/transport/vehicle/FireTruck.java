package com.example.transport.vehicle;

import com.example.transport.person.FireFighter;

/**
 * A kind of Car. Can only carry FireFighter passengers. Because this class
 * fixes the type parameter to FireFighter, board(Person) or
 * board(PoliceOfficer) will not even compile - the restriction is
 * enforced by the type system, not by a runtime check.
 */
public class FireTruck extends Car<FireFighter> {
    public FireTruck(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}
