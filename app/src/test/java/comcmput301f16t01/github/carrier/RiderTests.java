package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by kiete on 10/16/2016.
 */

public class RiderTests {

    @After
    public void clean() {
        UserController uc = new UserController();
        uc.reset();
    }

    @Test
    public void RiderConstructorTest() {
        String name = "kieter";
        String password = "bennettIsBae123";
        String email = "kieter@kieter.me";
        String phoneNumber = "7801234567";
        Rider kieter = new Rider(name, password, email, phoneNumber);

        // Test equality of all the fields.
        assertEquals("Username is not equal", name, kieter.getUsername());
        assertEquals("Password is not equal", password, kieter.getPassword());
        assertEquals("Email is not equal", email, kieter.getEmail());
        assertEquals("Phone is not equal", phoneNumber, kieter.getPhone());
    }

    @Test
    public void AuthenticationTest() {
        UserController uc = new UserController();
        String name = "kieter";
        String password = "bennettIsBae123";
        String email = "kieter@kieter.me";
        String phoneNumber = "7801234567";
        Rider kieterRider = new Rider(name, password, email, phoneNumber);
        RiderList riderList = new RiderList();

        // TODO should really be using something called addRider but that hasn't been made yet.
        riderList.add(kieterRider);
        assertTrue("The list does not contain kieter", riderList.contains(kieterRider));

        //TODO should really be using the controller to add stuff but that hasn't been implemented.
        Boolean authenticated = false;

        uc.getUsersHashMap().put(name, kieterRider);
        authenticated = uc.authenticate(name, password);
        assertTrue("Was not authenticated but credentials were right.", authenticated);

    }

}
