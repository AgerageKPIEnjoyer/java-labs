package com.example.transport.vehicle;

import com.example.transport.exception.NoAvailableSeatException;
import com.example.transport.exception.PassengerNotOnBoardException;
import com.example.transport.person.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A vehicle that carries passengers of type T (or any subtype of T).
 * T is bounded by Person, and each subclass (directly, or through the
 * intermediate Car class) fixes T to the passenger type it is allowed to
 * carry - that is how the transport restriction ("a fire truck can carry
 * only firefighters", etc.) is enforced by the compiler rather than by a
 * runtime check:
 *   - Bus                    extends Vehicle<Person>       -> any passenger accepted
 *   - Taxi                   extends Car<Person>            -> any passenger accepted
 *   - FireTruck              extends Car<Firefighter>       -> only Firefighter accepted
 *   - PoliceCar              extends Car<PoliceOfficer>     -> only PoliceOfficer accepted
 */
public abstract class Vehicle<T extends Person> {

    private final String registrationId;
    private final int maxSeats;
    private final List<T> passengers = new ArrayList<>();

    protected Vehicle(String registrationId, int maxSeats) {
        this.registrationId = registrationId;
        this.maxSeats = maxSeats;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getOccupiedSeats() {
        return passengers.size();
    }

    /** Read-only view of who is currently on board. */
    public List<T> getPassengers() {
        return Collections.unmodifiableList(passengers);
    }

    /**
     * Boards a passenger onto this vehicle.
     *
     * @throws NoAvailableSeatException if every seat is already occupied
     */
    public void board(T passenger) {
        if (getOccupiedSeats() >= maxSeats) {
            throw new NoAvailableSeatException(
                    registrationId + " has no available seats (capacity " + maxSeats + ").");
        }
        passengers.add(passenger);
    }

    /**
     * Removes a passenger from this vehicle.
     *
     * @throws PassengerNotOnBoardException if the passenger is not currently on board
     */
    public void disembark(T passenger) {
        if (!passengers.remove(passenger)) {
            throw new PassengerNotOnBoardException(
                    passenger + " is not on board " + registrationId + ".");
        }
    }

    @Override
    public String toString() {
        return String.format("%s[%s, %d/%d seats]",
                getClass().getSimpleName(), registrationId, getOccupiedSeats(), maxSeats);
    }
}
