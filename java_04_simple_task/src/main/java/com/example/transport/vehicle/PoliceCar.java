package com.example.transport.vehicle;

import com.example.transport.person.PoliceOfficer;

/**
 * A kind of Car. Can only carry PoliceOfficer passengers. Because this
 * class fixes the type parameter to PoliceOfficer, board(Person) or
 * board(Firefighter) will not even compile - the restriction is enforced
 * by the type system, not by a runtime check.
 */
public class PoliceCar extends Car<PoliceOfficer> {
    public PoliceCar(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}
