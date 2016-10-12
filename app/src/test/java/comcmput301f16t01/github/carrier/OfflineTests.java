package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class OfflineTests {

    @After
    public void reset() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     * US 08.01.01 As an driver, I want to see requests that I already accepted while offline.
     */
    @Test
    public void OfflineSeeRequestsTest() {
        Rider rider = new Rider("Kieter");
        Location start = new Location();
        Location end = new Location();
        Request request = new Request(rider, start, end);


    }

    /**
     * US 08.02.01 As a rider, I want to see requests that I have made while offline.

     */
    @Test
    public void test_08_02_01() {

    }

    /**
     * US 08.03.01 As a rider, I want to make requests that will be sent once I get connectivity again.
     */
    @Test
    public void test_08_03_01() {

    }

    /**
     * US 08.04.01 As a driver, I want to accept requests that will be sent once I get connectivity again.
     */
    @Test
    public void test_08_04_01() {

    }

}
