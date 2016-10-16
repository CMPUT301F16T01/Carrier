package comcmput301f16t01.github.carrier;

/**
 * Created by Ben on 2016-10-07.
 */
public class Rider extends User {
    private boolean notify = false;

    /**
     * Constructor, requires username, password, email, and phone number.
     * @param inputUsername The username
     * @param inputPassword The password
     * @param inputEmail The e-mail
     * @param inputPhoneNumber The phone number
     */
    public Rider(String inputUsername, String inputPassword, String inputEmail, String inputPhoneNumber) {
        super(inputUsername, inputPassword, inputEmail, inputPhoneNumber);
    }


    /**
     * Constructor, only requires username.
     * @param username The username.
     */
    public Rider(String username) {
        super( username );
    }

    public boolean hasNotification() {
        return notify;
    }
}
