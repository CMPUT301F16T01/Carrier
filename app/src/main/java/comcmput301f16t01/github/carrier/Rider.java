package comcmput301f16t01.github.carrier;

/**
 * Created by Ben on 2016-10-07.
 */
public class Rider extends User {
    private boolean notify = false;

    // TODO 
    public Rider(String inputUsername, String inputPassword, String inputEmail, String inputPhoneNumber) {
        super(inputUsername, inputPassword, inputEmail, inputPhoneNumber);
    }

    public Rider(String username) {
        super( username );
    }

    public boolean hasNotification() {
        return notify;
    }
}
