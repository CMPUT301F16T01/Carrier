package comcmput301f16t01.github.carrier;

import java.util.Random;

/**
 * calculates an estimated fare between two locations.
 */
public class FareCalculator {

    // Constants for fare, in USD
    static final double COST_PER_MIN = 0.15;
    static final double COST_PER_KM = 0.56;
    static final double BOOKING_FEE = 1.65;
    static final int MIN_FARE = (int) 5.15 * 100;

    /**
     * Creates a new object fare calculator, used to estimate a fair fare for a ride between
     * start and end.
     */
    public FareCalculator() {
    }

    public int getEstimate(double distance, double duration) {
        /* TODO: once we get routing working on our map we'll fetch the distance and duration */
        /* Assuming we're using OSM we can do road.mLength (a string, in km) and road.mDuration
        (a string) to get distance and pass it into this function for now I'll arbitrarily set
        distance and time - Kieter*/

        Random rand = new Random();
        // Distance is in km
        // Currently decided randomly
        //distance = 5 + (50 - 5) * rand.nextDouble();
        // Duration is in sec
        // Currently decided randomly
        //duration = 300 + (2400 - 300) * rand.nextDouble();

        /* Formula from http://www.ridesharingdriver.com/how-much-does-uber-cost-uber-fare-estimator/
        fare = base fare + (cost per minute * time in ride) + (cost per km * ride distance) + booking fee */
        /* Calculate fare and the the larger of fare vs minimum fare we multiply by 100 because
        we said we would */
        int calculatedFare = (int) Math.round(((BOOKING_FEE + (COST_PER_MIN * duration) +
                (COST_PER_KM * distance)) * 100) * 100) / 100; // *100/100 to round to two decimals.
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
