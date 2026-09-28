package com.example.transport.vehicle;

import com.example.transport.person.Person;

/**
 * A car: the intermediate class between Vehicle and Taxi/PoliceCar/
 * FireTruck in the hierarchy. It stays generic so each
 * concrete subclass can still fix its own allowed passenger type.
 */
public abstract class Car<T extends Person> extends Vehicle<T> {
    protected Car(String registrationId, int maxSeats) {
        super(registrationId, maxSeats);
    }
}

