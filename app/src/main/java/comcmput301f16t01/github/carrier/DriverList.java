package comcmput301f16t01.github.carrier;

import java.util.ArrayList;

/**
 * Created by meind on 2016-10-11.
 */

public class DriverList {

    protected ArrayList<Driver> drivers = null;

    public Driver getDriver(Integer position) {
        return drivers.get(position);
    }
}
