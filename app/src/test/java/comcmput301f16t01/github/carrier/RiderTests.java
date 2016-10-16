package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

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
        Rider kieter = new Rider(name, password, email, phoneNumber);
        Boolean authenticated = false;

        //TODO finish this test.
        //authenticated =

    }

}
