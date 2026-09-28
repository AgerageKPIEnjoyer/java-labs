package com.example.transport.vehicle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.example.transport.core.Road;
import com.example.transport.exception.NoAvailableSeatException;
import com.example.transport.exception.PassengerNotOnBoardException;
import com.example.transport.person.FireFighter;
import com.example.transport.person.Person;
import com.example.transport.person.PoliceOfficer;

class VehicleTest {

    private Bus bus;
    private Taxi taxi;
    private FireTruck fireTruck;
    private PoliceCar policeCar;

    @BeforeEach
    void setUp() {
        bus = new Bus("BUS-1", 3);
        taxi = new Taxi("TAXI-1", 2);
        fireTruck = new FireTruck("FIRE-1", 2);
        policeCar = new PoliceCar("POLICE-1", 2);
    }

    // -----------------------------------------------------------
    // Basic seat accounting
    // -----------------------------------------------------------

    @Test
    void newVehicleHasNoOccupiedSeats() {
        assertEquals(3, bus.getMaxSeats());
        assertEquals(0, bus.getOccupiedSeats());
    }

    // -----------------------------------------------------------
    // Boarding: a Bus/Taxi may carry any kind of passenger
    // -----------------------------------------------------------

    @Test
    void busCanBoardAnyPassengerType() {
        bus.board(new Person("Alice"));
        bus.board(new FireFighter("Bob"));
        bus.board(new PoliceOfficer("Carol"));

        assertEquals(3, bus.getOccupiedSeats());
    }

    @Test
    void taxiCanBoardAnyPassengerType() {
        taxi.board(new Person("Dave"));
        taxi.board(new FireFighter("Erin"));

        assertEquals(2, taxi.getOccupiedSeats());
    }

    // -----------------------------------------------------------
    // Boarding: FireTruck / PoliceCar restrictions
    // -----------------------------------------------------------

    @Test
    void fireTruckCanBoardFirefighters() {
        fireTruck.board(new FireFighter("Frank"));
        fireTruck.board(new FireFighter("Grace"));

        assertEquals(2, fireTruck.getOccupiedSeats());
    }

    @Test
    void policeCarCanBoardPoliceOfficers() {
        policeCar.board(new PoliceOfficer("Heidi"));

        assertEquals(1, policeCar.getOccupiedSeats());
    }

    // NOTE: There is no test here for "fireTruck.board(new Person(...))"
    // or "policeCar.board(new Firefighter(...))" because those calls are
    // rejected at COMPILE TIME - FireTruck extends Car<Firefighter> and
    // PoliceCar extends Car<PoliceOfficer>, so the inherited board(...)
    // method simply doesn't accept the other passenger types. That is the
    // whole point of enforcing the restriction through generics rather
    // than through a runtime check.

    // -----------------------------------------------------------
    // Hierarchy shape: Taxi, PoliceCar and FireTruck
    // extend the intermediate Car class.
    // -----------------------------------------------------------

    @Test
    void taxiPoliceCarAndFireTruckAreCars() {
        assertInstanceOf(Car.class, taxi);
        assertInstanceOf(Car.class, policeCar);
        assertInstanceOf(Car.class, fireTruck);
    }

    // -----------------------------------------------------------
    // Boarding beyond capacity
    // -----------------------------------------------------------

    @Test
    void boardingBeyondCapacityThrows() {
        fireTruck.board(new FireFighter("Frank"));
        fireTruck.board(new FireFighter("Grace"));

        NoAvailableSeatException ex = assertThrows(NoAvailableSeatException.class,
                () -> fireTruck.board(new FireFighter("Ivan")));
        assertTrue(ex.getMessage().contains("FIRE-1"));
        assertEquals(2, fireTruck.getOccupiedSeats());
    }

    // -----------------------------------------------------------
    // Disembarking
    // -----------------------------------------------------------

    @Test
    void disembarkingBoardedPassengerFreesASeat() {
        PoliceOfficer heidi = new PoliceOfficer("Heidi");
        policeCar.board(heidi);
        assertEquals(1, policeCar.getOccupiedSeats());

        policeCar.disembark(heidi);
        assertEquals(0, policeCar.getOccupiedSeats());
        assertFalse(policeCar.getPassengers().contains(heidi));
    }

    @Test
    void disembarkingPassengerNotOnBoardThrows() {
        PoliceOfficer heidi = new PoliceOfficer("Heidi");
        // heidi was never boarded onto policeCar

        PassengerNotOnBoardException ex = assertThrows(PassengerNotOnBoardException.class,
                () -> policeCar.disembark(heidi));
        assertTrue(ex.getMessage().contains("POLICE-1"));
    }

    @Test
    void seatFreedByDisembarkingCanBeReusedByBoarding() {
        FireFighter frank = new FireFighter("Frank");
        FireFighter grace = new FireFighter("Grace");
        fireTruck.board(frank);
        fireTruck.board(grace);

        fireTruck.disembark(frank);
        // now there is one free seat again
        fireTruck.board(new FireFighter("Ivan"));

        assertEquals(2, fireTruck.getOccupiedSeats());
    }

    // -----------------------------------------------------------
    // Road: counting humans across a mix of vehicle types
    // -----------------------------------------------------------

    @Test
    void roadCountsHumansAcrossDifferentVehicleTypes() {
        Road road = new Road();

        bus.board(new Person("Alice"));
        bus.board(new FireFighter("Bob"));
        road.addCarToRoad(bus); // 2 people

        taxi.board(new Person("Dave"));
        road.addCarToRoad(taxi); // 1 person

        fireTruck.board(new FireFighter("Frank"));
        fireTruck.board(new FireFighter("Grace"));
        road.addCarToRoad(fireTruck); // 2 people

        policeCar.board(new PoliceOfficer("Heidi"));
        road.addCarToRoad(policeCar); // 1 person

        assertEquals(6, road.getCountOfHumans());
    }

    @Test
    void roadWithNoVehiclesHasZeroHumans() {
        Road road = new Road();
        assertEquals(0, road.getCountOfHumans());
    }

    @Test
    void roadCountUpdatesAfterDisembarking() {
        Road road = new Road();
        Person alice = new Person("Alice");
        bus.board(alice);
        bus.board(new Person("Dave"));
        road.addCarToRoad(bus);

        assertEquals(2, road.getCountOfHumans());

        bus.disembark(alice);
        assertEquals(1, road.getCountOfHumans());
    }
}
