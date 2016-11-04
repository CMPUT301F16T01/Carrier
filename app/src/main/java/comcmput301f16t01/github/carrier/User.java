package comcmput301f16t01.github.carrier;

/**
 * Abstract base class for a user of Carrier.
 *
 * @see User
 * @see User
 */

public class User {
    private String username;
    private String email;
    private String phoneNumber;

    /**
     * For use with Elastic Search, is the unique ID given to it
     */
    private String elasticID;

    //TODO we should probably say what is and isn't a valid username, email, and phone number.

    /**
     * Constructor, requires username, email, and phone number.
     *
     * @param inputUsername    The username
     * @param inputEmail       The e-mail
     * @param inputPhoneNumber The phone number
     */
    public User(String inputUsername, String inputEmail, String inputPhoneNumber) {
        this.username = inputUsername;
        this.email = inputEmail;
        this.phoneNumber = inputPhoneNumber;
    }

    public User() {
        this.username = "default_name";
        // TODO this method was implemented to create a default method for extending classes. Probably needs refactoring.
    }

    public User(String name) {
        this.username = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }


    public boolean hasNotifications() {
        return false;
    }

    public void setId(String id) {
        elasticID = id;
    }

    public String getId() {
        return elasticID;
    }
}
