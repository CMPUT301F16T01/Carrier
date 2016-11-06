package comcmput301f16t01.github.carrier;

import org.junit.Test;
import static comcmput301f16t01.github.carrier.FareCalculator.COST_PER_KM;
import static comcmput301f16t01.github.carrier.FareCalculator.COST_PER_MIN;
import static org.junit.Assert.*;

public class FareCalculatorTests {

    @Test
    public void getEstimateTest() {
        Location a = new Location();
        Location b = new Location();
        FareCalculator fc = new FareCalculator(a, b);

        // Using the function defined in the class
        int fairFare = fc.getEstimate(5, 300);
        // Using the formula
        int realFare = (int) Math.round(((FareCalculator.BOOKING_FEE + (FareCalculator.COST_PER_MIN * 300) +
                (FareCalculator.COST_PER_KM * 5)) * 100) * 100) / 100;
        // Assure that the formula and the function produce the same result
        assertEquals("Fare and calcualted fair unequal", realFare, fairFare);

        // A trip that goes nowhere, of course, this can't ever happen
        int isItMinFare = fc.getEstimate(0, 0);
        // Sees if the nowhere trip charges the minimum fare
        assertEquals("It's not minFare!", FareCalculator.MIN_FARE, isItMinFare);
    }

}
