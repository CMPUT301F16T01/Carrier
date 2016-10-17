package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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

        //TODO implement this in usercontroller.java adding to rider should add to user too.
        uc.getRiderList().add(kieterRider);
        uc.getUserList().add(kieterRider);

        // Assuring the list contains the added element
        assertTrue("The list does not contain kieter", uc.getUserList().contains(kieterRider));
        assertEquals("The first thing is not kieter", uc.getUserList().get(0), kieterRider);

        // Try authenticating using the correct username and password
        Boolean authenticated = false;
        authenticated = uc.authenticate(name, password);

        // Test authenticating with the right credentials
        assertTrue("Not authenticated but the credentials were right." + uc.getUserList(), authenticated);
        Boolean authenticated2 = uc.authenticate(name + "1", password + "1");
        //Test authenticating with the wrong credentials
        assertFalse("Authenticated, but the credentials were wrong.", authenticated2);

    }
}
