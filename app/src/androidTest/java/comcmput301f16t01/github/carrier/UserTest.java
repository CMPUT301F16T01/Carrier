package comcmput301f16t01.github.carrier;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Tests for User Use Cases
 *      1) Tests that username is unique among all users.
 *      2) Tests that a user's info can be edited
 *
 */
public class UserTest extends ApplicationTest {
    private final String testUserUsername = "userTestUser@randomString9898989898";
    private final String testUserEmail = "helloWorld@email.com";
    private final String testUserPhoneNum = "7357-7357";
    private final String vehicleDesc = "Imaginary, you think I have money for that?";

    @Override
    protected void setUp() throws Exception {
        // Set up a user to use with the controller
        UserController.createNewUser( testUserUsername, testUserEmail, testUserPhoneNum, vehicleDesc );
        chillabit();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        UserController.logOutUser();
        UserController.deleteUser( "234567890aswedxcftgvbhujnmko" );
        UserController.deleteUser( testUserUsername );
        super.tearDown();
    }

    // abstracts reused code to prevent mistakes and aid in readability of tests
    // Makes the current thread sleep for the specified amount of time (in ms)
    private void chillabit() {
        try {
            Thread.sleep((long) 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Test1
     * Testing that we don't allow users to create a username that someone already has.
     * (createNewUser returns null if there was no errors).
     */
    public void testUniqueUsername() {
        UserController.logOutUser();
        String result = UserController.createNewUser( "234567890aswedxcftgvbhujnmko",
                testUserEmail, testUserPhoneNum, vehicleDesc);
        assertTrue( "Adding a user should have returned null (success)",
                result == null);
        UserController.logOutUser();
        result = UserController.createNewUser( "234567890aswedxcftgvbhujnmko",
                testUserEmail, testUserPhoneNum, vehicleDesc);
        assertTrue( "Adding a user should have returned an error (not null, failure)",
                result == null);
    }

    /**
     * Tests the functionality of editing a user in elastic search
     * @throws InterruptedException
     */
    public void testEditUserTask() throws InterruptedException {
        UserController.logInUser( testUserUsername );
        String newEmail = "new@test.com";
        String newPhone = "000000000";
        UserController.editUser(newEmail, newPhone);
        UserController.logOutUser();
        chillabit();
        User user = UserController.findUser( testUserUsername ); // elastic search component
        assertTrue( "Email did not change",
                user.getEmail().equals(newEmail));
        assertTrue( "phone number did not change",
                user.getPhone().equals(newPhone));
    }
}
