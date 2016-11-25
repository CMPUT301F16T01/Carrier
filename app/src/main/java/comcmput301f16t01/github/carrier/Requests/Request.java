package comcmput301f16t01.github.carrier.Requests;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.CarrierLocation;
import comcmput301f16t01.github.carrier.FareCalculator;
import comcmput301f16t01.github.carrier.Users.User;
import io.searchbox.annotations.JestId;

import static comcmput301f16t01.github.carrier.Requests.Request.Status.CONFIRMED;

/**
 * Represents a request for a ride.
 */
public class Request {
    /**
     * OPEN:        A user has made the request but no drivers have accepted.
     * OFFERED:     One or more drivers have offered to fulfill the request.
     * CONFIRMED:   The user has chosen a driver and accepted one request.
     * COMPLETE:    The user has gotten to their destination (and payed?)
     * PAID:
     * CANCELLED:   The rider has cancelled their request
     */
    public enum Status {
        OPEN,
        OFFERED,
        CONFIRMED,
        COMPLETE,
        PAID,
        CANCELLED
    }

    /** The current status of a this request */
    private Status status = Status.OPEN;

    /** The user who made the request. */
    private User rider;

    /**
     * The driver that the user has chosen to drive for the request
     */
    private User chosenDriver = null;
    /** A list of drivers who have offered to complete the request (but have not been accepted) */
    private ArrayList<User> offeringDrivers;

    /** The "from" of the request, where the user wants to go from */
    private CarrierLocation start;

    /** The "end" of the request, where the user want to go */
    private CarrierLocation end;

    /** A description provided by the rider */
    private String description;

    /** The price the requesting user is willing to pay for the request to be complete */
    private int fare;

    /** The distance in kilometers */
    private double distance;

    private Double[] location;

    /** For use with Elastic Search, is the unique ID given to it */
    @JestId
    private String elasticID = null;

    // Constructor with description
    public Request(@NonNull User requestingRider, @NonNull CarrierLocation requestedStart,
                   @NonNull CarrierLocation requestedEnd, String description) {
        this.rider = requestingRider;
        this.start = requestedStart;
        this.end = requestedEnd;
        this.description = description;
        this.offeringDrivers = new ArrayList<>();
        this.location = new Double[2];
        this.location[0] = requestedStart.getLongitude();
        this.location[1] = requestedStart.getLatitude();
    }

    // Constructor without description TODO delete?
    @Deprecated
    public Request(User rider, CarrierLocation start, CarrierLocation end) {
        this.rider = rider;
        this.start = start;
        this.end = end;
        this.offeringDrivers = new ArrayList<>();
        this.description = "";
        this.location = new Double[2];
        this.location[0] = start.getLongitude();
        this.location[1] = start.getLatitude();
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Attempts to set the status of the request.
     *
     * @throws IllegalStateException if the status could not move from a certain state to another.
     *
     * @param newStatus the new status you would like to set the request to.
     */
    public void setStatus(Status newStatus) {
        // Does not allow the canceling of completed requests or paid requests
        if ((this.status == Status.COMPLETE) && (newStatus == Status.CANCELLED))
            throw new IllegalStateException( "You cannot change a request from COMPLETE to CANCELLED" );
        if ((this.status == Status.PAID) && (newStatus == Status.CANCELLED)) {
            throw new IllegalStateException( "You cannot change a request from PAID to CANCELLED" );
        }
        this.status = newStatus;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public void setChosenDriver(User driver) {
        this.chosenDriver = driver;
        setStatus(CONFIRMED);
    }

    public User getChosenDriver() {
        return this.chosenDriver;
    }

    public ArrayList<User> getOffers() {
        return offeringDrivers;
    }

    public User getRider() {
        return this.rider;
    }

    public CarrierLocation getStart() {
        return this.start;
    }

    public CarrierLocation getEnd() {
        return this.end;
    }

    public User getConfirmedDriver() {
        return chosenDriver;
    }

    public ArrayList<User> getOfferedDrivers() {
        return this.offeringDrivers;
    }

    public String getDescription() {
        return description;
    }

    public int getFare() {
        return fare;
    }

    public void setId(String id) {
        this.elasticID = id;
    }

    public String getId() {
        return elasticID;
    }

    /** @return The string representation of a request, in plain text. */
    @Override
    public String toString() {
        return "Request From: " + rider.getUsername() + "\n" +
                "Description: " + description + "\n" +
                "Price: " + FareCalculator.toString( fare ) + "\n" +
                "Distance: " + distance + "km\n" +
                "Price per KM: " + FareCalculator.toString((int) (fare/distance));
    }

    /**
     * Will add the driver to the list of offering drivers.
     *
     * @throws IllegalArgumentException If the driver has already offered or you pass in null.
     * @param offeredDriver The driver that is making the offer.
     */
    public void addOfferingDriver(@NonNull User offeredDriver) {
        // If the status is not OPEN or OFFERED, we cannot add another driver.
        if (status != Request.Status.OPEN && status != Request.Status.OFFERED) {
            throw new IllegalArgumentException( "The request is not in the correct state to take more offers" );
        }

        // If there is a chosenDriver (but the status was wrong) we should throw an error.
        if( chosenDriver != null ) {
            throw new IllegalArgumentException( "This request already has a chosen driver." );
        }

        // If the driver is already a part of the request we should throw an error.
        if( !hasOfferingDriver( offeredDriver )) {
            offeringDrivers.add(offeredDriver);
        } else {
            throw new IllegalArgumentException( "You are already offering to complete this request." );
        }

        setStatus( Request.Status.OFFERED );
    }

    /**
     * Confirms a driver to be the designated driver for a request.
     *
     * @throws IllegalArgumentException if the driver has not made an offer first or the driver is null.
     *
     * @param confirmedDriver the driver you wish to set as the confirmed driver.
     */
    public void confirmDriver( @NonNull User confirmedDriver) {
        if ( chosenDriver != null ) {
            throw new IllegalArgumentException( "There is already a chosen driver for this request." );
        }
        chosenDriver = confirmedDriver;
        setStatus(CONFIRMED); // TODO, dangerous to do this because of edge cases?
    }

    /**
     * Check if driver is already inside the list of offering drivers for this request.
     *
     * @return True if the passed driver has made an offer, otherwise false.
     */
    public boolean hasOfferingDriver( User driver ) {
        for ( User driverOffering : offeringDrivers ) {
            if ( driverOffering.getUsername().equals(driver.getUsername()) ) {
                return true;
            }
        }
        return false;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }
}
