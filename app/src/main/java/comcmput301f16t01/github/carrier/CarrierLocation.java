package comcmput301f16t01.github.carrier;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

/**
 * Represents a geo-location on the globe.
 * Includes an address string.
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

    // TODO http://stackoverflow.com/questions/454908/split-java-string-by-new-line
    public void setAddress(String address) {
        if(address != null) {
            this.address = address;
            this.shortAddress = address.split("\\r?\\n")[0];
        }
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
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
