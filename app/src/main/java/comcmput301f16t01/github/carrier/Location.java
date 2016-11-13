package comcmput301f16t01.github.carrier;

/**
 * Represents a geo-location on the globe.
 */
@Deprecated
public class Location {
    private double latitude;
    private double longitude;

    public Location() {
        // get current location
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLocation(double latitude, double longitude) {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
