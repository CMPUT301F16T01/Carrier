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

    /**
     * Constructor, requires username, password, email, and phone number.
     * @param inputUsername The username
     * @param inputPassword The password
     * @param inputEmail The e-mail
     * @param inputPhoneNumber The phone number
     */
    public Driver(String inputUsername, String inputPassword, String inputEmail, String inputPhoneNumber) {
        super(inputUsername, inputPassword, inputEmail, inputPhoneNumber);
    }

    /**
     * Constructor, only requires username.
     * @param username The username.
     */
    public Driver(String username) {
        super( username );
    }

    public boolean hasNotifications() {
        return  notify;
    }
}
