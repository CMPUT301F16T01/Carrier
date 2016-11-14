package comcmput301f16t01.github.carrier;

import android.location.Location;

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

        User riderOne = new User("username");
        assertTrue(uc.uniqueRiderUsername(riderOne));

        User riderTwo = new User("username");
        assertFalse(uc.uniqueRiderUsername(riderTwo));
    }

    /**
     * As a user, I want a profile with a unique username and my contact information.
     * Related: US 03.01.01
     */
    @Test
    public void uniqueDriverUsername() {
        UserController uc = new UserController();

        User driverOne = new User("username");
        assertTrue(uc.uniqueDriverUsername(driverOne));

        User driverTwo = new User("username");
        assertFalse(uc.uniqueDriverUsername(driverTwo));
    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editRiderInfo() {
        UserController uc = new UserController();
        User rider = new User("Mandy");
        uc.addRider(rider);

        String email = "start email";
        String phone = "start phone";
        uc.setEmail(rider, email);
        uc.setPhone(rider, phone);

        assertTrue(rider.getUsername().equals("start username"));
        assertTrue(rider.getEmail().equals("start email"));
        assertTrue(rider.getPhone().equals("start phone"));

        String difUsername = "another username";
        String difEmail = "another email";
        String difPhone = "another phone";

        uc.setUsername(rider, difUsername);
        uc.setEmail(rider, difEmail);
        uc.setPhone(rider, difPhone);

        assertTrue(rider.getUsername().equals("another username"));
        assertTrue(rider.getEmail().equals("another email"));
        assertTrue(rider.getPhone().equals("another phone"));

    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editDriverInfo() {
        UserController uc = new UserController();
        User driver = new User("Mandy");
        uc.addDriver(driver);

        String email = "start email";
        String phone = "start phone";
        uc.setEmail(driver, email);
        uc.setPhone(driver, phone);

        assertTrue(driver.getUsername().equals("Mandy"));
        assertTrue(driver.getEmail().equals(email));
        assertTrue(driver.getPhone().equals(phone));

        String difUsername = "another username";
        String difEmail = "another email";
        String difPhone = "another phone";


        uc.setUsername(driver, difUsername);
        uc.setEmail(driver, difEmail);
        uc.setPhone(driver, difPhone);

        assertTrue(driver.getUsername().equals("another username"));
        assertTrue(driver.getEmail().equals("another email"));
        assertTrue(driver.getPhone().equals("another phone"));

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
        User you = new User("you");
        String description = "thing";
        Request request = new Request(you, new CarrierLocation(), new CarrierLocation(), description);
        UserController uc = new UserController();
        User driver = new User("Mandy");
        uc.addDriver(driver);
        uc.addRider(you);

        String email = "email";
        String phone = "phone";

        uc.setEmail(driver, email);
        uc.setPhone(driver, phone);


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
        User rider = new User("Sarah");
        String description = "thing";
        Request request = new Request(rider, new CarrierLocation(), new CarrierLocation(), description);
        UserController uc = new UserController();
        User you = new User("you");
        uc.addDriver(you);
        uc.addRider(rider);

        String email = "email";
        String phone = "phone";

        uc.setEmail(rider, email);
        uc.setPhone(rider, phone);


        assertTrue(request.getRider().getEmail().equals("email"));
        assertTrue(request.getRider().getPhone().equals("phone"));

    }




}
