package comcmput301f16t01.github.carrier;

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
    static final int CANCELLED = 9;        // The user has gotten to their destination (and payed?)

    private int status = OPEN;

    public Request(Rider rider, Location start, Location end) {

    }

    public int getStatus() {
        return status;
    }
    // TODO confirm these as the states for a Request.
}