package comcmput301f16t01.github.carrier;

import android.location.Location;

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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getLatLong() {
        return "(" + String.valueOf(getLatitude()) + ", " +
                String.valueOf(getLongitude()) + ")";
    }
}
