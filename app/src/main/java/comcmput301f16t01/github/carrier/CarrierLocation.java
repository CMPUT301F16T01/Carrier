package comcmput301f16t01.github.carrier;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

/**
 * <p>Represents a geo-location on the globe. Includes an address string.</p>
 * </br>
 * <p>Based on: <a href="http://stackoverflow.com/questions/454908/split-java-string-by-new-line">Split Java String by New Line</a></p>
 * <p>Author: <a href="http://stackoverflow.com/users/18393/cletus">cletus</a></p>
 * <p>Posted on: January 18th, 2016</p>
 * <p>Retrieved on: November 27th, 2016</p>
 */
public class CarrierLocation extends Location {
    private String address;
    private String shortAddress;

    public CarrierLocation() {
        // get current location
        super("");
    }

    public CarrierLocation(double latitude, double longitude) {
        super("");
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public String getAddress() {
        return address;
    }

    // see code attribution
    public void setAddress(String address) {
        if(address != null) {
            this.address = address;
            // short address is just the first line of the address string
            this.shortAddress = address.split("\\r?\\n")[0];
        }
    }

    public String getShortAddress() {
        return shortAddress;
    }

    /**
     * For use when an address is null. Returns a string of a lat/long tuple.
     * @return String
     */
    public String getLatLong() {
        return "(" + String.valueOf(getLatitude()) + ", " +
                String.valueOf(getLongitude()) + ")";
    }

    @Override
    public String toString() {
        if(address != null) {
            return getAddress();
        } else {
            return getLatLong();
        }
    }
}
