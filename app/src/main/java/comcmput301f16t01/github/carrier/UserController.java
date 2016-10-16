package comcmput301f16t01.github.carrier;

import android.content.Context;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by meind on 2016-10-11.
 *
 * Singleton Pattern
 * * modifies/returns a RequestList model
 * * @see Request
 * * @see RequestList
 */

public class UserController {
    private static RiderList riderList = null;
    private static DriverList driverList = null;
    private static Dictionary users = null;
    private static User loggedInUser = null;


    public UserController() {
        if (riderList == null) {
            riderList = new RiderList();
        }
        if (driverList == null) {
            driverList = new DriverList();
        }
    }

    /**
     * @return the RiderList held by this controller.
     */
    public static RiderList getRiderList() {
        if (riderList == null) {
            riderList = new RiderList();
        }
        return riderList;
    }

    /**
     * @return the DriverList held by this controller.
     */
    public static DriverList getDriverList() {
        if (driverList == null) {
            driverList = new DriverList();
        }
        return driverList;
    }

    /**
     * Making sure that the username is unique across all riders
     * otherwise it throws an exception
     *
     * return True if it is unique and False if it is similar
     *
     * @param rider
     */
    public boolean uniqueRiderUsername(Rider rider) {
        return false;
    }

    /**
     * Making sure that the username is unique across all drivers
     * otherwise it throws an exception
     *
     * return True if it is unique and False if it is similar
     *
     * @param driver
     */
    public boolean uniqueDriverUsername(Driver driver) {
        return false;
    }


    public void setEmail(User user, String email) {
        user.setEmail(email);
    }

    public void setPhone(User user, String phone) {
        user.setPhone(phone);
    }

    public void setUsername(User user, String username) { user.setUsername(username);
    }

    public void addDriver(Driver driver) {
    }

    public void addRider(Rider rider) {
    }

    /**
     * authenticate is called when the user needs to login. Checks to see if the combination of
     * username and password that hte user entered is valid. It throws an exception when the
     * combination of username and password is wrong.
     *
     * @author Kieter
     * @since Saturday October 15th, 2016
     *
     * @see LoginActivity
     */
    public boolean authenticate(String usernameString, String passwordString) throws NullPointerException {
        // Try checking if the username actually exists (is contained in the dictionary)
        try {
            String realPassword = (String) users.get(usernameString);
        } catch (NullPointerException noKey) {
            return false;
        }

        // The user is contained in the dictionary, check if the passwords match.
        String realPassword = (String) users.get(usernameString);
        // If they match, the user is successfully authenticated, otherwise they are not.
        if (passwordString.equals(realPassword)) {
            return true;
        } else {
            return false;
        }
    }
}
