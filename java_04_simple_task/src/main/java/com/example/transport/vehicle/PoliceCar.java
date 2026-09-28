package com.example.transport.vehicle;

import com.example.transport.person.PoliceOfficer;

/**
 * A kind of Car. Can only carry PoliceOfficer passengers.
 */
public class PoliceCar extends Car<PoliceOfficer> {
    public PoliceCar(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}
