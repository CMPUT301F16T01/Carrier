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
        Rider riderOne = new Rider("start username");
        riderOne.setEmail("start email");
        riderOne.setPhone("start phone");

        assertTrue(riderOne.getUsername().equals("start username"));
        assertTrue(riderOne.getEmail().equals("start email"));
        assertTrue(riderOne.getPhone().equals("start phone"));

        riderOne.setUsername("another username");
        riderOne.setEmail("another email");
        riderOne.setPhone("another phone");

        assertTrue(riderOne.getUsername().equals("another username"));
        assertTrue(riderOne.getEmail().equals("another email"));
        assertTrue(riderOne.getPhone().equals("another phone"));

    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editDriverInfo() {
        Driver driverOne = new Driver("start username");
        driverOne.setEmail("start email");
        driverOne.setPhone("start phone");

        assertTrue(driverOne.getUsername().equals("start username"));
        assertTrue(driverOne.getEmail().equals("start email"));
        assertTrue(driverOne.getPhone().equals("start phone"));

        driverOne.setUsername("another username");
        driverOne.setEmail("another email");
        driverOne.setPhone("another phone");

        assertTrue(driverOne.getUsername().equals("another username"));
        assertTrue(driverOne.getEmail().equals("another email"));
        assertTrue(driverOne.getPhone().equals("another phone"));

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
        rc.confirmDriver(request, driver);

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
        rc.confirmDriver(request, you);

        assertTrue(request.getRider().getEmail().equals("email"));
        assertTrue(request.getRider().getPhone().equals("phone"));

    }




}
