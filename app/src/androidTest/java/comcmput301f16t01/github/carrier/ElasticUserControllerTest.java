package comcmput301f16t01.github.carrier;

import java.util.Random;

/**
 * Created by kiete on 11/13/2016.
 */

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ElasticUserControllerTest extends ApplicationTest {
    UserController uc = new UserController();

    @Override
    protected void setUp() throws InterruptedException {
        // The logged in user
        uc.createNewUser("ElasticUserControllerTest", "test@test.com", "1234567890");
        Thread.sleep(3000);
        uc.logInUser("ElasticUserControllerTest");
        // A user to remove later
        uc.createNewUser("ElasticUserControllerTest2", "delete@test.com", "1234567890");
    }

    public void testAddUserTask() throws InterruptedException, ExecutionException {
        User elasticUser = uc.findUser("ElasticUserControllerTest");
        Thread.sleep(3000);

        // Ensure that the user was actually put into elastic search
        assertEquals("User in elastic search and copy of user are not the same",
                uc.getLoggedInUser().getUsername(), elasticUser.getUsername());


    }

//    public void testEditUserTask() throws InterruptedException {
//        String newEmail = "new@test.com";
//        String newPhone = "000000000";
//        uc.editUser(newEmail, newPhone);
//        Thread.sleep(3000);
//
//        assertEquals("The email did not update", newEmail, uc.getLoggedInUser().getEmail());
//        assertEquals("The phone did not update", newPhone, uc.getLoggedInUser().getPhone());
//    }


}
