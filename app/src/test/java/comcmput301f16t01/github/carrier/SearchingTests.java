package comcmput301f16t01.github.carrier;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchingTests {
    @After
    public void clean() {
        RequestController rc = new RequestController();
        rc.reset();
    }

    /**
     * As a driver, I want to browse and search for open requests by geo-location.
     * Related: US 04.01.01
     */
    @Test
    public void testDriverSearchByLocation() {

    }

    /**
     * As a driver, I want to browse and search for open requests by keyword.
     * Related: US 04.02.01
     */
    @Test
    public void testDriverSearchByKeyword() {

    }
}
