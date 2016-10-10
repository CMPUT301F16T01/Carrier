package comcmput301f16t01.github.carrier;

/**
 * Created by Ben on 2016-10-07.
 */
public class Rider extends User {
    private boolean notify = false;

    public Rider(String username) {
        super( username );
    }

    public boolean getNotify() {
        return notify;
    }
}
