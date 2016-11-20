package comcmput301f16t01.github.carrier.User;

import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;

import comcmput301f16t01.github.carrier.UserList;

/**
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
     *
     * @return Returns the logged in ser
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Deprecated: use logInUser instead.
     * @see #logInUser(String)
     */
    @Deprecated
    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Making sure that the username is unique across all riders
     * otherwise it throws an exception
     * <p>
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
     * <p>
     * return True if it is unique and False if it is similar
     *
     * @param driver
     */
    @Deprecated
    public boolean uniqueDriverUsername(User driver) {
        return false;
    }

    private static boolean checkUniqueUsername(String username) {
        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();
        fut.execute(username);
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
     *
     * @param username
     * @return
     */
    public boolean logInUser(String username) {
        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();
        fut.execute(username);
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

    public void setUsername(User user, String username) {
        user.setUsername(username);
    }

    @Deprecated
    public void addDriver(User driver) {
    }

    @Deprecated
    public void addRider(User rider) {
    }

    /**
     * authenticate is called when the user needs to login. Checks to see if the username
     * the user entered is valid. It throws an exception when the
     * username is wrong. Authenticate also sets the loggedInUser upon
     * successful login.
     *
     * @param usernameString The username the user attempts to login with
     * @throws NullPointerException Happens when the user enters a username with a username that
     *                              does not exist.
     * @author Kieter
     * @see LoginActivity
     * @since Saturday October 15th, 2016
     */
    @Deprecated
    //TODO re-implement after elastic search is all good
    public boolean authenticate(String usernameString) throws NullPointerException {
        User attemptedUser = null;

        // Iterate over all the users, checking to see if the given username is the users
        for (User user : this.getUserList()) {
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
     *
     */
    public void reset() {
        // TODO this never had the option to clear UserList.
    }


    public static String checkValidInputs(String username, String email, String phoneNumber) {
        // TODO testing offline behaviour
        User newUser = new User();

        // Trim leading and trailing whitespace
        email = email.trim();
        phoneNumber = phoneNumber.trim();
        username = username.trim();

        newUser.setEmail(email);
        newUser.setPhone(phoneNumber);
        newUser.setUsername(username);

        // TODO more checks need to be done when adding a user, not important.

        // Ensure no entries are blank
        if( email.equals("") || phoneNumber.equals("") || username.equals("") ) {
            return "Username, email, and phone number may not be blank.";
        }

        // Ensure a username is unique
        if (!checkUniqueUsername(username)) {
            return "That username is already taken!";
        }

        // Check if the email matches the email pattern
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            return "That doesn't look like a valid email!";
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            return "That doesn't look like a valid phone number!";
        }
        return null;
    }





    /**
     * Attempt to create a new user.
     *
     * @param username
     * @param email
     * @param phoneNumber
     * @return
     */
    public static void createNewUser(String username, String email, String phoneNumber, String vehicleDescription) {
        // TODO testing offline behaviour
        User newUser = new User();

        // Trim leading and trailing whitespace
        email = email.trim();
        phoneNumber = phoneNumber.trim();
        username = username.trim();

        newUser.setEmail(email);
        newUser.setPhone(phoneNumber);
        newUser.setUsername(username);
        newUser.setVehicleDescription(vehicleDescription);

        ElasticUserController.AddUserTask aut = new ElasticUserController.AddUserTask();
        aut.execute(newUser);

        while (newUser.getId() == null) {
            // waiting...
        }

        loggedInUser = newUser;
    }

    /**
     * Searches elastic search for the given username and returns a User object matching that username
     * @param username The username to search for
     * @return
     */
    public User findUser(String username) {
        User foundUser = null;

        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();

        fut.execute(username);
        try {
            foundUser = fut.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundUser;
    }

    /**
     * Edits the logged in user given a newEmail and newPhone
     * @param newEmail The email to change to
     * @param newPhone The phone number to change to
     */
    public static void editUser(String newEmail, String newPhone) {
        ElasticUserController.EditUserTask eut = new ElasticUserController.EditUserTask();
        eut.execute(UserController.getLoggedInUser().getId(), newEmail, newPhone);
    }

    /**
     * Deletes a user from elastic search
     * @param usernameToDelete The username to delete from ElasticSearch
     */
    public void deleteUser(String usernameToDelete) {
        ElasticUserController.DeleteUserTask dut = new ElasticUserController.DeleteUserTask();
        dut.execute(usernameToDelete);
    }

    public void logOutUser() {
        loggedInUser = null;
    }
}
