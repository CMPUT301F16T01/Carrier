package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

import static java.security.AccessController.getContext;

/**
 * Calculates an estimated fare between two locations and provides a method for converting
 * a fare into a float string.
 *
 * See code attribution in Wiki: <a href="https://github.com/CMPUT301F16T01/Carrier/wiki/Code-Re-Use#farecalculator">FareCalculator</a>
 * Formula from: <a href="http://www.ridesharingdriver.com/how-much-does-uber-cost-uber-fare-estimator/">How much does Uber cost? Uber fare estimator</a>
 * Formula: fare = base fare + (cost per minute * time in ride) + (cost per km * ride distance) + booking fee
 */
public class FareCalculator {

    // Constants for calculating the fare.
    static final double COST_PER_MIN = 0.15;
    static final double COST_PER_KM = 0.56;
    static final double BOOKING_FEE = 1.65;
    static final int MIN_FARE = (int) 5.00 * 100;

    /**
     * Static method takes in distance and duration and returns an estimate of a fare to post with.
     *
     * Formula from http://www.ridesharingdriver.com/how-much-does-uber-cost-uber-fare-estimator/
     * fare = base fare + (cost per minute * time in ride) + (cost per km * ride distance) + booking fee
     *
     * @param distance The distance of the ride in kilometers
     * @param duration The duration of the ride in seconds
     * @return Returns whatever is greater: the calculated far or the minimum fare.
     */
    public static int getEstimate(double distance, double duration) {
        // Calculate fare and the the larger of fare vs minimum fare, we multiply by 100 to get an integer
        int calculatedFare = (int) Math.round(((BOOKING_FEE + (COST_PER_MIN * duration) +
                (COST_PER_KM * distance)) * 100) * 100) / 1000;  // We divide by 1000 to round to two decimal places

        return Math.max(calculatedFare, MIN_FARE);
    }

    /**
     * Converts integer fare into a readable string (price format).
     * @param intFare the fare as an integer (the dollar value * 100)
     * @return A string representing the dollar amount with decimal.
     */
    public static String toString(int intFare) {
        double fare = ((double) intFare)/100;
        String str = String.format(Locale.getDefault(), "%d",(long)fare) + ".";
        String dec = String.format(Locale.getDefault(), "0%.0f",(fare%1)*100);
        // format the fare as a string with 2 decimal points
        str +=  dec.substring(dec.length()-2, dec.length());
        return str;
    }
}
