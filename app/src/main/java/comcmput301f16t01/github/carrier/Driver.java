package comcmput301f16t01.github.carrier;

/**
 * Represents a driver in Carrier.
 * Drivers can...
 *
 * @see Request
 * @see User
 */
public class Driver extends User {
    private boolean notify = false;

    public Driver(String inputUsername, String inputPassword, String inputEmail, String inputPhoneNumber) {
        super(inputUsername, inputPassword, inputEmail, inputPhoneNumber);
    }

    public Driver(String username) {
        super( username );
    }

    public boolean hasNotifications() {
        return  notify;
    }
}
