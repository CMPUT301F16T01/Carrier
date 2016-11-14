package comcmput301f16t01.github.carrier;

import java.util.Random;

/**
 * Created by kiete on 11/13/2016.
 */

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class ElasticUserControllerTest extends ApplicationTest {

    private void setUpUser() throws InterruptedException {
        UserController uc = new UserController();
        uc.createNewUser("ElasticUserControllerTest", "test@test.com", "1234567890");
        Thread.sleep(3000);
    }

    public void testAddUserTask() throws InterruptedException, ExecutionException {
        UserController uc = new UserController();
        User user = new User("ElasticUserControllerTest", "test@test.com", "1234567890");
        User elasticUser = uc.findUser("ElasticUserControllerTest");
        Thread.sleep(3000);

        assertEquals("Users are not the same", user.getUsername(), elasticUser.getUsername());
    }
}
