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
 * US 01.03.01 [offer recieved notification]
 * @see NotificationTest#testRiderGetNotified()
 *
 * US 01.04.01 [rider cancelling a request]
 * @see RequestTest#testCancelRequest()
 *
 * US 01.06.01 [Calculate a fair fare]
 * @see FareCalculatorTests#testGetEstimate()
 *
 * US 01.07.01 [Confirm completion and pay] : (proof of backend)
 * @see RequestTest#testRequestStatus()
 *
 * US 01.08.01 [Confirm driver]
 * @see RequestTest
 *
 * US 01.09.01 [See driver's vehicle] : (proof of backend)
 * @see UserTest
 *
 * Status ==========================================================================================
 * US 02.01.01 [see status of request] : (proof of backend)
 * @see RequestTest#testRequestStatus()
 *
 * UserProfile =====================================================================================
 * US 03.01.01 [Unique username and contact info]
 * @see UserTest#testUniqueUsername()
 *
 * US 03.02.01 [Edit own contact info]
 * @see UserTest#testEditUserTask()
 *
 * US 03.03.01 [See contact info of user] (proof of backend)
 * @see UserTest
 *
 * US 03.04.01 [Provide vehicle details] (proof of backend)
 * @see UserTest
 *
 * Searching =======================================================================================
 * US 04.01.01 [Browse reqeusts by geo-location]
 * @see SearchingTests#testDriverSearchByLocation()
 * @see SearchingTests#testDriverSearchByKeywordWithConfirmed()
 *
 * US 04.02.01 [Browse by keyword]
 * @see SearchingTests#testDriverSearchByKeyword()
 * @see SearchingTests#testDriverSearchByKeywordWithConfirmed()
 *
 * US 04.03.01
 * @see SearchingTests#testPriceFiltering()
 *
 * US 04.04.01 [see addresses of requests] : (proof of backend)
 * US 04.05.01 [search by address] : (proof of backend)
 * @see SearchingTests
 * @see LocationTests
 *
 * Accepting =======================================================================================
 * US 05.01.01 [Accept a request as a driver]
 * @see RequestTest#testRequestStatus()
 * @see RequestTest#testAddingDriverToRequest()
 *
 * US 05.02.01 [View pending offers as driver]
 * @see RequestTest#testGetRequestsWhereOffered()
 *
 * US 05.03.01 [See if acceptance was accepted] : (proof of backend)
 * @see RequestTest#testRequestStatus()
 * @see RequestTest#testAddingDriverToRequest()
 *
 * US 05.04.01 [Notification when offer was accepted]
 * @see NotificationTest#testDriverGetNotified()
 *
 * Offline behavior ================================================================================
 * US 08.01.01 [I want see requests I accepted while offline]
 * @see OfflineTests#testSavingLoadingOfflineDriverRequests()
 *
 * US 08.02.01 [See requests made while offline]
 * @see OfflineTests#testSavingLoadingOfflineRiderRequests() t
 *
 * US 08.03.01 [make requests offline] : (proof of backend)
 * @see OfflineTests
 *
 * US 08.04.01 [make offers offline] : (proof of backend)
 * @see OfflineTests
 *
 * Location ========================================================================================
 * US 10.01.01 [Specify on map for request] : (proof of backend)
 * @see SearchingTests
 *
 * US 10.02.01 [View start and end for request] : (proof of backend)
 * @see SearchingTests
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}