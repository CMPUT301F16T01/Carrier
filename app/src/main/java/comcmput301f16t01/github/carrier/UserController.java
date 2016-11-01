package comcmput301f16t01.github.carrier;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *
 * Singleton Pattern
 * * modifies/returns a RequestList model
 * * @see Request
 * * @see RequestList
 */
public class UserController {
    private static UserList userList = null;
    private static User loggedInUser = null;



    public UserController() {
        if (userList == null) {
            userList = new UserList();
        }
    }

    public static ArrayList<User> getUserList() {
        if (userList == null) {
            userList = new UserList();
        }
        return userList.getUsers();
    }

    // TODO removed the static designation so it could be accessed from MakeRequestActivity...is this okay?
    /**
     * Will return the current logged in user.
     * @return Returns the logged in ser
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Will set the logged in user.
     * @param user The user who is being set to the logged in user.
     */
    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Making sure that the username is unique across all riders
     * otherwise it throws an exception
     *
     * return True if it is unique and False if it is similar
     *
     * @param rider
     */
    @Deprecated
    public boolean uniqueRiderUsername(User rider) {
        return false;
    }

    /**
     * Making sure that the username is unique across all drivers
     * otherwise it throws an exception
     *
     * return True if it is unique and False if it is similar
     *
     * @param driver
     */
    @Deprecated
    public boolean uniqueDriverUsername(User driver) {
        return false;
    }

    private boolean checkUniqueUsername( String username ) {
        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();
        fut.execute( username );
        User foundUser = null;
        try {
            foundUser = fut.get();
        } catch (Exception e) {
            Log.i("checkUniqueUsername", "bad error");
        }
        if (foundUser == null) {
            return true;
        }
        return false;
    }

    // TODO simplify above and below things (share the same code...)
    /**
     * returns false if no user with that username, otherwise sets them as logged in
     * @param username
     * @return
     */
    public boolean logInUser( String username ) {
        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();
        fut.execute( username );
        User foundUser = null;
        try {
            foundUser = fut.get();
        } catch (Exception e) {
            Log.i("logInUser", "bad error");
        }
        if (foundUser == null) {
            return false;
        }
        loggedInUser = foundUser;
        return true;
    }

    public void setEmail(User user, String email) {
        user.setEmail(email);
    }

    public void setPhone(User user, String phone) {
        user.setPhone(phone);
    }

    public void setUsername(User user, String username) { user.setUsername(username);
    }

    public void addDriver(User driver) {
    }

    public void addRider(User rider) {
    }

    /**
     * authenticate is called when the user needs to login. Checks to see if the username
     * the user entered is valid. It throws an exception when the
     * username is wrong. Authenticate also sets the loggedInUser upon
     * successful login.
     *
     * @author Kieter
     * @since Saturday October 15th, 2016
     * @see LoginActivity
     * @throws NullPointerException Happens when the user enters a username with a username that
     * does not exist.
     * @param usernameString The username the user attempts to login with
     */
    @Deprecated
    //TODO re-implement after elastic search is all good
    public boolean authenticate(String usernameString) throws NullPointerException {
        User attemptedUser = null;

        // Iterate over all the users, checking to see if the given username is the users
        for (User user: this.getUserList()) {
            // If there is a username match, store the user
            if (usernameString.equals(user.getUsername())) {
                this.loggedInUser = user;
                return true;
            }
        }
        // Otherwise this is a faulty login.
        return false;
    }

    /**
     * resets the UserController. Primarily used for testing.
     * @since Sunday October 16th, 2016
     */
    public void reset() {
        // TODO this never had the option to reset UserList.
    }

    /**
     * Attempt to create a new user...
     * @param username
     * @param email
     * @param phoneNumber
     * @return
     */
    public String createNewUser(String username, String email, String phoneNumber) {
        User newUser = new User();
        newUser.setEmail( email );
        newUser.setPhone( phoneNumber );
        newUser.setUsername( username );

        // TODO more checks need to be done when adding a user, not important.

        if ( !checkUniqueUsername( username ) ) {
            return "That username is already taken!";
        }

        ElasticUserController.AddUserTask aut = new ElasticUserController.AddUserTask();
        aut.execute( newUser );
        while ( newUser.getId() == null ) {
            // waiting...
        }
        loggedInUser = newUser;
        return null;
    }

    public void logOutUser() {
        loggedInUser = null;
    }
}
