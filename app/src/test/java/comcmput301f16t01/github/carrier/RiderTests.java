package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import comcmput301f16t01.github.carrier.User.User;
import comcmput301f16t01.github.carrier.User.UserController;

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
        String email = "kieter@kieter.me";
        String phoneNumber = "7801234567";
        User kieter = new User(name, email, phoneNumber);

        // Test equality of all the fields.
        assertEquals("Username is not equal", name, kieter.getUsername());
        assertEquals("Email is not equal", email, kieter.getEmail());
        assertEquals("Phone is not equal", phoneNumber, kieter.getPhone());
    }

    @Test
    public void AuthenticationTest() {
        UserController uc = new UserController();
        String name = "kieter";
        String email = "kieter@kieter.me";
        String phoneNumber = "7801234567";
        User kieterRider = new User(name, email, phoneNumber);
        RiderList riderList = new RiderList();

        // TODO should really be using something called addRider but that hasn't been made yet.
        riderList.add(kieterRider);
        assertTrue("The list does not contain kieter", riderList.contains(kieterRider));

        // TODO: test driven development: don't add the feature in your test. uc.addRider( riderObject ) should have added it to the UserList as well.
        //uc.getRiderList().add(kieterRider);
        //uc.getUserList().add(kieterRider);
        // TODO: MVC, and passwords, remove deprecated RiderList
        // TODO: proper usage of UserController: uc.addUser( userObject );

        // Assuring the list contains the added element
        assertTrue("The list does not contain kieter", uc.getUserList().contains(kieterRider));
        assertEquals("The first thing is not kieter", uc.getUserList().get(0), kieterRider);

        // Try authenticating using the correct username
        Boolean authenticated = false;
        authenticated = uc.authenticate(name);

        // Test authenticating with the right credentials
        assertTrue("Not authenticated but the credentials were right." + uc.getUserList(), authenticated);
        Boolean authenticated2 = uc.authenticate(name + "1");
        //Test authenticating with the wrong credentials
        assertFalse("Authenticated, but the credentials were wrong.", authenticated2);

    }
}
