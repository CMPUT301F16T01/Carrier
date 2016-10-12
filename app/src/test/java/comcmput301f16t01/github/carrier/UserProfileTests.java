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
        uc.getLoggedInRider().setEmail("start email");
        uc.getLoggedInRider().setPhone("start phone");

        assertTrue(uc.getLoggedInRider().getUsername().equals("start username"));
        assertTrue(uc.getLoggedInRider().getEmail().equals("start email"));
        assertTrue(uc.getLoggedInRider().getPhone().equals("start phone"));

        uc.getLoggedInRider().setUsername("another username");
        uc.getLoggedInRider().setEmail("another email");
        uc.getLoggedInRider().setPhone("another phone");

        assertTrue(uc.getLoggedInRider().getUsername().equals("another username"));
        assertTrue(uc.getLoggedInRider().getEmail().equals("another email"));
        assertTrue(uc.getLoggedInRider().getPhone().equals("another phone"));

    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editDriverInfo() {
        UserController uc = new UserController();
        uc.getLoggedInDriver().setEmail("start email");
        uc.getLoggedInDriver().setPhone("start phone");

        assertTrue(uc.getLoggedInDriver().getUsername().equals("start username"));
        assertTrue(uc.getLoggedInDriver().getEmail().equals("start email"));
        assertTrue(uc.getLoggedInDriver().getPhone().equals("start phone"));

        uc.getLoggedInDriver().setUsername("another username");
        uc.getLoggedInDriver().setEmail("another email");
        uc.getLoggedInDriver().setPhone("another phone");

        assertTrue(uc.getLoggedInDriver().getUsername().equals("another username"));
        assertTrue(uc.getLoggedInDriver().getEmail().equals("another email"));
        assertTrue(uc.getLoggedInDriver().getPhone().equals("another phone"));

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
        RequestController rc = new RequestController();
        Driver driver = new Driver("Mandy");
        driver.setEmail("email");
        driver.setPhone("phone");

        rc.addRequest(request);
        rc.addDriver(request, driver);
        rc.acceptDriver(request, driver);

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
        RequestController rc = new RequestController();
        Driver you = new Driver("you");
        rider.setEmail("email");
        rider.setPhone("phone");

        rc.addRequest(request);
        rc.addDriver(request, you);
        rc.acceptDriver(request, you);

        assertTrue(request.getRider().getEmail().equals("email"));
        assertTrue(request.getRider().getPhone().equals("phone"));

    }




}
