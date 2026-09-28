package com.example.transport.vehicle;

import com.example.transport.person.Person;

/**
 * Extends Vehicle directly (a Bus is not a Car). Carries any kind of passenger.
 */
public class Bus extends Vehicle<Person> {
    public Bus(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}

