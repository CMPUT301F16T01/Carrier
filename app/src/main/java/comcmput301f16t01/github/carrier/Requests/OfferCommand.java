package comcmput301f16t01.github.carrier.Requests;

import comcmput301f16t01.github.carrier.Users.User;

/**
 * Stores information to make an offer command in the future. This is used for offline
 * functionality to queue offer commands to be made.
 */
public class OfferCommand {
    private Request request;
    private User driver;

    public OfferCommand(Request request, User driver) {
        this.request = request;
        this.driver = driver;
    }

    public Request getRequest() {
        return request;
    }

    public User getDriver() {
        return driver;
    }
}
