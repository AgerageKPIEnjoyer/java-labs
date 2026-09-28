package com.example.transport.vehicle;

import com.example.transport.person.Person;

/**
 * A kind of Car. Carries any kind of passenger.
 */
public class Taxi extends Car<Person> {
    public Taxi(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}

