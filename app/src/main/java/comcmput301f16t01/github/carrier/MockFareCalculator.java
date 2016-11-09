package comcmput301f16t01.github.carrier;

/**
 * calculates an estimated fare between two locations
 * MOCK CLASS, creates fake data
 */
public class MockFareCalculator extends FareCalculator {

    /**
     * @param start
     * @param end
     */
    public MockFareCalculator(Location start, Location end) {
        super();
    }

    // This not an @Override because FareCalculator does not have a getEstimate() it has a getEstimate(double, double)
    public int getEstimate() {
        double upper = 20000;
        double lower = 1;
        // next line from https://goo.gl/mpm34N, author: user2512642, retrieved on October 31st, 2016
        return (int)((Math.random() * (upper - lower)) + lower);
    }
}
