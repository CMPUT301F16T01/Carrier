package comcmput301f16t01.github.carrier;

import org.junit.Test;

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
    public void uniqueUsername() {
        Rider riderOne = new Rider("username");
        Request request = new Request(riderOne, new Location(), new Location());
        RequestController rc = new RequestController();
        rc.addRequest(request);

        assertEquals("There should only be one request returned.",
                1, rc.getRequests(riderOne).size());

        // Add a request to ensure we get back specific requests of a user.
        rc.addRequest(new Request(new Rider("otherRider"), new Location(), new Location()));

        // Ensures that we still only get one request for our user, with a second user in the system
        assertEquals("There should only be one request returned.",
                1, rc.getRequests(riderOne).size());

        // Checks if the request put in is the same that returns
        assertEquals("getRequests should return requests for a specified user",
                request, rc.getRequests(riderOne).get(0));

        // TODO include "get open requests? or just check if .isOpen() (?)

    }

    /**
     * As a user, I want to edit the contact information in my profile.
     * Related: US 03.02.01
     */
    @Test
    public void editInfo() {


    }

    /**
     * As a user, I want to, when a username is presented for a thing, retrieve and show its contact information.
     * Related: US 03.03.01
     */
    @Test
    public void showUsername() {


    }




}
