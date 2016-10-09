package comcmput301f16t01.github.carrier;

/**
 * Represents a request for a ride.
 */
public class Request {
    // These are not static because they will be used with an instance of Request?
    // i.e. request.setStatus( request.OPEN );

    public final int OPEN = 1;            // A user has made the request but no drivers have accepted.
    public final int ACCEPTED = 2;        // One or more drivers have accepted the request.
    public final int CONFIRMED = 3;       // The user has chosen a driver and accepted one request.
    public final int COMPLETE = 4;        // The user has gotten to their destination (and payed?)

    public Request(Rider rider, Location start, Location end) {

    }
    // TODO confirm these as the states for a Request.
}