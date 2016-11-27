package comcmput301f16t01.github.carrier;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 *
 * All tests extend ApplicationTest. The following is a list of test cases representing use cases.
 * If you receive the "no tests in suite" error, check that you have an instrumentation runner set
 * up (click the drop down between Make Project (hammer button) and Run (play button), click
 * Edit Configurations, and you should see the text box for the option.
 *
 * US 01.01.01 [request a ride]: (proof of back-end.
 * US 01.02.01 [see current requests] : (proof of back-end)
 * @see RequestTest
 *
 * US 01.03.01 [driver notification]
 * @see NotificationTest#testDriverGetNotified()
 *
 * US 01.04.01 [rider cancelling a request]
 * @see RequestTest#testCancelRequest()
 *
 *
 *
 *
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}