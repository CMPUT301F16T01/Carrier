package comcmput301f16t01.github.carrier;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;


public class UserTest extends ApplicationTest {
    private final String testUserUsername = "userTestUser@randomString9898989898";
    private final String testUserEmail = "helloWorld@email.com";
    private final String testUserPhoneNum = "7357-7357";
    private final String vehicleDesc = "Imaginary, you think I have money for that?";

    @Override
    protected void setUp() throws Exception {
        // Set up a user to use with the controller
        UserController.createNewUser( testUserUsername, testUserEmail, testUserPhoneNum, vehicleDesc );
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        UserController.logOutUser();
        UserController.deleteUser(testUserUsername);

        super.tearDown();
    }


    /**
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
        String newEmail = "new@test.com";
        String newPhone = "000000000";
        UserController.editUser(newEmail, newPhone);
        Thread.sleep(1000);

        // See if both fields were edited
        assertEquals("The email did not update", newEmail, UserController.getLoggedInUser().getEmail());
        assertEquals("The phone did not update", newPhone, UserController.getLoggedInUser().getPhone());
    }

    /**
     * Tests the functionality of deleting a user from elastic search
     * @throws InterruptedException
     */
    public void testUserDeleteTask() throws InterruptedException {
        UserController.deleteUser("ElasticUserControllerTest2");
        User deletedUser = UserController.findUser("ElasticUserControllerTest2");
        Thread.sleep(1000);

        // See if the deleted user was actually deleted
        assertEquals("The deleted user still exists", null, deletedUser);
    }
}
