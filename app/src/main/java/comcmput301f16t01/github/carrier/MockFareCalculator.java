package comcmput301f16t01.github.carrier;

/**
 * Calculates an estimated fare between two locations.
 * MOCK CLASS, creates fake data.
 *
 * See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#mockfarecalculator">MockFareCalculator</a>
 *
 * Based on: <a href="http://stackoverflow.com/questions/5271598/java-generate-random-number-between-two-given-values">Java Generate Random Number Between Two Given Values</a>
 * Author: <a href="http://stackoverflow.com/users/2512642/user2512642">user2512642</a>
 * Posted on: June 22nd, 2013
 * Retrieved on: October 31st, 2016
 */
public class MockFareCalculator extends FareCalculator {

    public MockFareCalculator() {
        super();
    }

    //@Override
    public int getEstimate() {
        double upper = 20000;
        double lower = 1;
        return (int)((Math.random() * (upper - lower)) + lower);
    }
}
