package comcmput301f16t01.github.carrier;

import java.util.Random;

/**
 * Created by kiete on 11/13/2016.
 */

import java.util.Random;

public class ElasticUserControllerTest extends ApplicationTest {

    public void testAddUserTask() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()_-";
        int length = 5;
        Random random = new Random();

        char[] usernameArray = new char[length];
        for (int i = 0; i < length; i++) {
            usernameArray[i] = characters.charAt(random.nextInt(characters.length()));
        }
        String username = new String(usernameArray);

        User user = new User();
        user.setUsername(username);

        UserController uc = new UserController();
        uc.createNewUser(username, "@", "1");

        thread.sleep()

        User elasticUser = uc.findUser(username);

//        System.out.println(user.getUsername());
//        System.out.println(elasticUser.getUsername());

        assertEquals("Users are not the same", user.getUsername(), elasticUser.getUsername());
    }
}
