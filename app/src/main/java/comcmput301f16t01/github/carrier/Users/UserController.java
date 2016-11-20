package comcmput301f16t01.github.carrier.Users;

import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

/**
 * Singleton Pattern
 * Holds an instance of an user (the user of the app), allows for logging in users with elastic
 * search or memory.
 */
public class UserController {
    private static User loggedInUser = null;

    private UserController() { /* prevent UserController instantiation */ }

    /**
     * Will return the singleton instance of the current logged in user.
     *
     * @return Returns the currently logged in user
     * @throws IllegalAccessError if there is no currently logged in user.
     * @see #logInUser(String)
     */
    public static User getLoggedInUser() {
        if (loggedInUser == null) {
            throw new IllegalAccessError("You must first log in a user before getting an instance of one.");
        }
        return loggedInUser;
    }

    /**
     * Attempt to create a new user, if successful it will set the new user to the logged in user.
     *
     * @param username    The username that the new user will have
     * @param email       The email the new user will have
     * @param phoneNumber The phone number the new user will have.
     * @return A string containing an error message if unsuccessful, otherwise null.
     */
    public static String createNewUser(@NonNull String username, @NonNull String email, @NonNull String phoneNumber) {
        User newUser = new User();

        // Trim leading and trailing whitespace
        email = email.trim();
        phoneNumber = phoneNumber.trim();
        username = username.trim();

        newUser.setEmail(email);
        newUser.setPhone(phoneNumber);
        newUser.setUsername(username);

        // Ensure no entries are blank
        if (email.equals("") || phoneNumber.equals("") || username.equals("")) {
            return "Username, email, and phone number may not be blank.";
        }

        // Ensure a username is unique
        if (!checkUniqueUsername(username)) {
            return "That username is already taken!";
        }

        // Check if the email matches the email pattern and phone pattern
        if (!validateEmail(email)) {
            return "That doesn't look like a valid email!";
        }
        if (!validatePhone(phoneNumber)) {
            return "That doesn't look like a valid phone number!";
        }

        ElasticUserController.AddUserTask aut = new ElasticUserController.AddUserTask();
        aut.execute(newUser);

        // TODO bad waiting. (isConnected here?)
        while (newUser.getId() == null) {
            // waiting...
        }

        loggedInUser = newUser; // set the logged in user to be the one we created.
        return null;
    }

    /**
     * returns true if the email matches a valid email pattern.
     */
    private static boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * returns true if the phone number matches a valid phone number pattern
     */
    private static boolean validatePhone(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    /**
     * Attempts to check elastic search for the passed username. Returns whether or not that username
     * was found.
     *
     * @param username The username you would like to check elastic search for.
     * @return True if that username was found, false otherwise.
     */
    private static boolean checkUniqueUsername(String username) {
        ElasticUserController.FindUserTask fut = new ElasticUserController.FindUserTask();
        fut.execute(username);
        User foundUser = null;
        try {
            foundUser = fut.get();
        } catch (Exception e) {
            Log.i("checkUniqueUsername", "bad error");
        }
        return (foundUser == null); // return the boolean result
    }

    /**
     * Edits the logged in user given a newEmail and newPhone
     *
     * @param newEmail The email to change to
     * @param newPhone The phone number to change to
     */
    public static void editUser(String newEmail, String newPhone) {
        // TODO this should modify the logged in user here, but it does not?
        ElasticUserController.EditUserTask eut = new ElasticUserController.EditUserTask();
        eut.execute(UserController.getLoggedInUser().getId(), newEmail, newPhone);
    }

    /**
     * Deletes a user from elastic search. (For testing only)
     *
     * @param usernameToDelete The username to delete from ElasticSearch
     */
    public static void deleteUser(String usernameToDelete) {
        ElasticUserController.DeleteUserTask dut = new ElasticUserController.DeleteUserTask();
        dut.execute(usernameToDelete);
    }

    /**
     * Logs out the current user by setting them to null.
     */
    public void logOutUser() {
        loggedInUser = null;
    }

    /**
     * Attempts to log in a user and set that user to the singleton instance.
     *
     * @param username The username you would like to try logging in with.
     * @return True if the user was successfully logged in, else false.
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

    /**
     * Searches elastic search for the given username and returns a User object matching that
     * username.
     *
     * @param username The username to search for
     * @return The specified User (by username)
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
}