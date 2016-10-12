package comcmput301f16t01.github.carrier;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Represents a request for a ride.
 */
public class Request {
    // These are not static because they will be used with an instance of Request?
    // i.e. request.setStatus( request.OPEN );
    static final int OPEN = 1;            // A user has made the request but no drivers have accepted.
    static final int ACCEPTED = 2;        // One or more drivers have accepted the request.
    static final int CONFIRMED = 3;       // The user has chosen a driver and accepted one request.
    static final int COMPLETE = 4;        // The user has gotten to their destination (and payed?)
    static final int PAID = 7;
    static final int CANCELLED = 9;        // The user has cancelled their request

    private int status = OPEN;
    private Rider rider;
    private Location start;
    private Location end;
    private String description;

    public Request(Rider rider, Location start, Location end, String description) {

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {

    }

    public int getFareEstimate() {
        FareCalculator fareCalc = new FareCalculator( start, end );
        return fareCalc.getEstimate();
    }

    public ArrayList<Driver> getOffers() {
        return new ArrayList<Driver>();
    }

    public Rider getRider() {
        return this.rider;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getEnd() {
        return this.end;
    }

    public void notifyRider() {

    }

    // TODO confirm these as the states for a Request.
}