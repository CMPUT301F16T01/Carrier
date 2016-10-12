package comcmput301f16t01.github.carrier;

import java.util.ArrayList;

/**
 * Created by meind on 2016-10-11.
 */

public class RiderList {

    protected ArrayList<Rider> riders = null;

    public Rider getRider(Integer position) {
        return riders.get(position);
    }
}
