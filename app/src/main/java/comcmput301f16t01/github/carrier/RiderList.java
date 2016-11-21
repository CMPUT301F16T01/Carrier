package comcmput301f16t01.github.carrier;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.Users.User;


/**
 * TODO: Remove/delete class when nothing references RiderList
 */
@Deprecated
public class RiderList extends ArrayList {

    /**
     * Contains
     */
    ArrayList<User> riders;

    public RiderList() {
        riders = new ArrayList<User>();
    }

}
