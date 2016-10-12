package comcmput301f16t01.github.carrier;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by meind on 2016-10-11.
 */

public class UserProfileTests {

    /**
     * As a user, I want a profile with a unique username and my contact information.
     * Related: US 03.01.01
     */
    @Test
    public void uniqueRiderUsername() {
        UserController uc = new UserController();

        Rider riderOne = new Rider("username");
        assertTrue(uc.uniqueRiderUsername(riderOne));

        Rider riderTwo = new Rider("username");
        assertFalse(uc.uniqueRiderUsername(riderTwo));
    }

    /**
     * As a user, I want a profile with a unique username and my contact information.
     * Related: US 03.01.01
     */
    @Test
    public void uniqueDriverUsername() {
        UserController uc = new UserController();

        Driver driverOne = new Driver("username");
        assertTrue(uc.uniqueDriverUsername(driverOne));

        Driver driverTwo = new Driver("username");
        assertFalse(uc.uniqueDriverUsername(driverTwo));
    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editRiderInfo() {
        UserController uc = new UserController();
        uc.getLoggedInUser().setEmail("start email");
        uc.getLoggedInUser().setPhone("start phone");

        assertTrue(uc.getLoggedInUser().getUsername().equals("start username"));
        assertTrue(uc.getLoggedInUser().getEmail().equals("start email"));
        assertTrue(uc.getLoggedInUser().getPhone().equals("start phone"));

        uc.getLoggedInUser().setUsername("another username");
        uc.getLoggedInUser().setEmail("another email");
        uc.getLoggedInUser().setPhone("another phone");

        assertTrue(uc.getLoggedInUser().getUsername().equals("another username"));
        assertTrue(uc.getLoggedInUser().getEmail().equals("another email"));
        assertTrue(uc.getLoggedInUser().getPhone().equals("another phone"));

    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editDriverInfo() {
        UserController uc = new UserController();
        uc.getLoggedInUser().setEmail("start email");
        uc.getLoggedInUser().setPhone("start phone");

        assertTrue(uc.getLoggedInUser().getUsername().equals("start username"));
        assertTrue(uc.getLoggedInUser().getEmail().equals("start email"));
        assertTrue(uc.getLoggedInUser().getPhone().equals("start phone"));

        uc.getLoggedInUser().setUsername("another username");
        uc.getLoggedInUser().setEmail("another email");
        uc.getLoggedInUser().setPhone("another phone");

        assertTrue(uc.getLoggedInUser().getUsername().equals("another username"));
        assertTrue(uc.getLoggedInUser().getEmail().equals("another email"));
        assertTrue(uc.getLoggedInUser().getPhone().equals("another phone"));

    }

    /**
     * As a user, I want to, when a username is presented for a thing, retrieve and show its contact information.
     * Related: US 03.03.01
     *
     * a rider will only ever see other drivers usernames. Therefore when they are going through
     * the request we just have to find the first driver and give up that info
     */
    @Test
    public void showUsernameIfyouAreRider() {
        Rider you = new Rider("you");
        Request request = new Request(you, new Location(), new Location());
        UserController uc = new UserController();
        Driver driver = new Driver("Mandy");


        uc.setEmail(driver, "email");
        uc.setPhone(driver, "phone");


        assertTrue(request.getOffers().get(0).getEmail().equals("email"));
        assertTrue(request.getOffers().get(0).getPhone().equals("phone"));

    }


    /**
     * As a user, I want to, when a username is presented for a thing, retrieve and show its contact information.
     * Related: US 03.03.01
     *
     * a driver will only ever see other riders usernames. Therefore when they are going through
     * the request we just have to find the rider and give up that info
     */
    @Test
    public void showUsernameIfyouAreDriver() {
        Rider rider = new Rider("Sarah");
        Request request = new Request(rider, new Location(), new Location());
        UserController uc = new UserController();
        Driver you = new Driver("you");
        
        uc.setEmail(rider, "email");
        uc.setPhone(rider, "phone");


        assertTrue(request.getRider().getEmail().equals("email"));
        assertTrue(request.getRider().getPhone().equals("phone"));

    }




}
