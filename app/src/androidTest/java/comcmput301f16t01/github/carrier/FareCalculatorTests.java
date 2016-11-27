package comcmput301f16t01.github.carrier;

public class FareCalculatorTests extends ApplicationTest {

    /**
     * Tests the the output of FareCalculator is the expected output (the formula)
     * Ensures there is a minimum fare check in place.
     */
    public void testGetEstimate() {
        // Using the function defined in the class
        int fairFare = FareCalculator.getEstimate(20, 7000);

        // Using the formula
        int realFare = (int) Math.round(((FareCalculator.BOOKING_FEE + (FareCalculator.COST_PER_MIN * 7000) +
                (FareCalculator.COST_PER_KM * 20)) * 100) * 100) / 100;
        realFare /= 10;

        // Assure that the formula and the function produce the same result
        assertEquals("Fare and calculated fair unequal", realFare, fairFare);

        // A trip that goes nowhere, of course, this can't ever happen
        int isItMinFare = FareCalculator.getEstimate(0, 0);

        // Sees if the nowhere trip charges the minimum fare
        assertEquals("It's not minFare!", FareCalculator.MIN_FARE, isItMinFare);
    }

}
