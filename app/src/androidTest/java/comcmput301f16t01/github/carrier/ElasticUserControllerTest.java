package comcmput301f16t01.github.carrier;

/**
 * Created by kiete on 11/13/2016.
 */

import java.util.concurrent.ExecutionException;

<<<<<<< HEAD
=======
import comcmput301f16t01.github.carrier.Users.User;
>>>>>>> master

public class ElasticUserControllerTest extends ApplicationTest {
    UserController uc = new UserController();

    @Override
    protected void setUp() throws InterruptedException {
        // The logged in user
        uc.createNewUser("ElasticUserControllerTest", "test@test.com", "1234567890");
        Thread.sleep(1000);
        uc.logInUser("ElasticUserControllerTest");
        // A user to remove later
        uc.createNewUser("ElasticUserControllerTest2", "delete@test.com", "1234567890");
        Thread.sleep(1000);
    }

    @Override
    protected void tearDown() {
        uc.deleteUser("ElasticUserControllerTest");
    }

    /**
     * Tests adding a user to elastic search
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void testAddUserTask() throws InterruptedException, ExecutionException {
        User elasticUser = uc.findUser("ElasticUserControllerTest");
        Thread.sleep(1000);

        // Ensure that the user was actually put into elastic search
        assertEquals("User in elastic search and copy of user are not the same",
                UserController.getLoggedInUser().getUsername(), elasticUser.getUsername());


    }

    /**
     * Tests the uniqueness of a username.
     */
    public void testUniqueUsername() {

//        Test for uniqueness, createNewUser returns the string "That username is already taken" if
//        a string is not unique
        assertEquals("The username is not unique.", "That username is already taken!", UserController.createNewUser
                ("ElasticUserControllerTest", "j@j.com", "1234567"));
    }

    /**
     * Tests the functionality of editing a user in elastic search
     * @throws InterruptedException
     */
    public void testEditUserTask() throws InterruptedException {
        String newEmail = "new@test.com";
        String newPhone = "000000000";
        uc.editUser(newEmail, newPhone);
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
        uc.deleteUser("ElasticUserControllerTest2");
        User deletedUser = uc.findUser("ElasticUserControllerTest2");
        Thread.sleep(1000);

        // See if the deleted user was actually deleted
        assertEquals("The deleted user still exists", null, deletedUser);
    }



}
