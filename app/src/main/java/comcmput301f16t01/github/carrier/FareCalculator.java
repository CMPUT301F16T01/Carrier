package comcmput301f16t01.github.carrier;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

import static java.security.AccessController.getContext;

/**
 * calculates an estimated fare between two locations.
 */
public class FareCalculator {

    // Constants for fare, in USD
    static final double COST_PER_MIN = 0.15;
    static final double COST_PER_KM = 0.56;
    static final double BOOKING_FEE = 1.65;
    static final int MIN_FARE = (int) 5.00 * 100;

    public static int getEstimate(double distance, double duration) {

        // Distance is in km
        // Duration is in sec

        /* Formula from http://www.ridesharingdriver.com/how-much-does-uber-cost-uber-fare-estimator/
        fare = base fare + (cost per minute * time in ride) + (cost per km * ride distance) + booking fee */
        /* Calculate fare and the the larger of fare vs minimum fare we multiply by 100 because
        we said we would */
        int calculatedFare = (int) Math.round(((BOOKING_FEE + (COST_PER_MIN * duration) +
                (COST_PER_KM * distance)) * 100) * 100) / 100; // *100/100 to round to two decimals.

        calculatedFare /= 10;
        // The least a fare could be.
        int minFare = MIN_FARE;

        int fare = Math.max(calculatedFare, minFare);

        return fare;
    }

    /**
     * Converts integer fare into a readable string (price format).
     * @param intFare
     * @return
     */
    public String toString(int intFare) {
        double fare = ((double) intFare)/100;
        String str = String.format("%d",(long)fare) + ".";
        String dec = String.format("0%.0f",(fare%1)*100);
        // format the fare as a string with 2 decimal points
        str +=  dec.substring(dec.length()-2, dec.length());
        return str;
    }
}
