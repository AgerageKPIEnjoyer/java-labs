package com.example.transport.core;

import com.example.transport.person.Person;
import com.example.transport.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * A stretch of road with vehicles on it. Vehicle is generic
 * (Vehicle<T extends Person>), so a raw "List<Vehicle>" would only
 * compile with unchecked-type warnings and would not let the list hold a
 * mix of Bus, Taxi, FireTruck and PoliceCar in a type-safe way. Using the
 * wildcard "List<Vehicle<? extends Person>>" instead lets the list hold
 * a vehicle carrying ANY passenger subtype, while still being fully
 * type-checked - we only ever read from these vehicles here (getOccupiedSeats()
 * doesn't depend on T), so an "extends" (producer) wildcard is exactly right.
 */

public class Road {

    public List<Vehicle<? extends Person>> carsInRoad = new ArrayList<>();

    /** Total number of passengers currently being transported by every vehicle on this road. */
    public int getCountOfHumans() {
        int total = 0;
        for (Vehicle<? extends Person> car : carsInRoad) {
            total += car.getOccupiedSeats();
        }
        return total;
    }

    /** Adds a vehicle (of any passenger type) to this road. */
    public void addCarToRoad(Vehicle<? extends Person> vehicle) {
        carsInRoad.add(vehicle);
    }
}

