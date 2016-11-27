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
 * Request Tests ===================================================================================
 * US 01.01.01 [request a ride]: (proof of backend)
 * US 01.02.01 [see current requests] : (proof of backend)
 * @see RequestTest
 *
 * US 01.03.01 [driver notification]
 * @see NotificationTest#testDriverGetNotified()
 *
 * US 01.04.01 [rider cancelling a request]
 * @see RequestTest#testCancelRequest()
 *
 * US 01.07.01 [Confirm completion and pay] : (proof of backend)
 * @see RequestTest#testRequestStatus()
 *
 * US 01.08.01 [Confirm driver]
 * @see RequestTest
 *
 * US 01.09.01 [See driver's vehicle] : (proof of backend)
 * TODO
 *
 * Status ==========================================================================================
 * US 02.01.01 [see status of request] : (proof of backend)
 * @see RequestTest#testRequestStatus()
 *
 * UserProfile =====================================================================================
 * US 03.01.01 [Unique username and contact info]
 * TODO
 *
 * US 03.02.01 [Edit own contact info]
 * TODO
 *
 * US 03.03.01 [See contact info of user]
 * TODO
 *
 * US 03.04.01 [Provide vehicle details]
 * TODO
 *
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}