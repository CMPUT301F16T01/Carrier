package comcmput301f16t01.github.carrier;

/**
 * Created by kiete on 11/13/2016.
 */
import java.util.concurrent.ExecutionException;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

public class ElasticUserControllerTest extends ApplicationTest {

    @Override
    protected void setUp() throws InterruptedException {
        // The logged in user
        UserController.createNewUser("ElasticUserControllerTest", "test@test.com", "1234567890", "Kia, Rio" );
        Thread.sleep(1000);
        //UserController.logInUser("ElasticUserControllerTest");
        // A user to remove later
        UserController.createNewUser("ElasticUserControllerTest2", "delete@test.com", "1234567890", "Kia, Rio" );
    }

    @Override
    protected void tearDown() {
        UserController.logOutUser();
        UserController.deleteUser("ElasticUserControllerTest");
    }


    /**
     * Tests the uniqueness of a username.
     */
    public void testUniqueUsername() {
//        Test for uniqueness, createNewUser returns the string "That username is already taken" if
//        a string is not unique
        assertEquals("The username is not unique.", "That username is already taken!",
                UserController.checkValidInputs("ElasticUserControllerTest", "j@j.com", "1234567"));
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
