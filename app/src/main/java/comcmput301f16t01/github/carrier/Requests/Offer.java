package comcmput301f16t01.github.carrier.Requests;

import comcmput301f16t01.github.carrier.User.User;

/**
 * Offer object, for storing offers to elastic search for a request from a driver.
 * This class is purely for the ease of using JestDroid to get these.
 * @see Request
 */
public class Offer {
    /** The username of the offering driver */
    private String offeringUser;

    /** The ID of the associated request the driver is offering to complete. */
    private String requestID;

    public Offer( Request request, User driver ) {
        if (request.getId() == null || driver.getUsername() == null ) {
            throw new IllegalArgumentException( "Neither the request ID nor the driver's username can be null." );
        }
        offeringUser = driver.getUsername();
        requestID = request.getId();
    }

    public String getRequestID() {
        return requestID;
    }

    public String getOfferingUser() {
        return offeringUser;
    }
}
