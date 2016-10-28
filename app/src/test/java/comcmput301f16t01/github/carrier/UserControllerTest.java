package comcmput301f16t01.github.carrier;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by michael on 27/10/16.
 */
public class UserControllerTest {
    @Test
    public void getLoggedInUser() {
        UserController uc = new UserController();
        User user = new User();
        user.setEmail("T@mail.com");
        user.setPhone("7801234567");
        user.setUsername("Mike");
        uc.setLoggedInUser(user);
        User user1 = UserController.getLoggedInUser();
        assertTrue("The users are not the same", user == user1);


    }

}