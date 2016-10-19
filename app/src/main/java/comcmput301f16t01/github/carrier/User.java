package comcmput301f16t01.github.carrier;
/**
 * Abstract base class for a user of Carrier.
 *
 * @see User
 * @see User
 */
public class User {
    protected String username;
    protected String password;
    protected String email;
    protected String phoneNumber;

    //TODO we should probably say what is and isn't a valid username, password, email, and phone number.
    /**
     * Constructor, requires username, password, email, and phone number.
     * @param inputUsername The username
     * @param inputPassword The password
     * @param inputEmail The e-mail
     * @param inputPhoneNumber The phone number
     */
    public User(String inputUsername, String inputPassword, String inputEmail, String inputPhoneNumber) {
        this.username = inputUsername;
        this.password = inputPassword;
        this.email = inputEmail;
        this.phoneNumber = inputPhoneNumber;
    }

    public User() {
        this.username = "default_name";
        // TODO this method was implemented to create a default method for extending classes. Probably needs refactoring.
    }

    public User(String name ) {
        this.username = name;
    }

    public void setUsername(String username) {this.username = username; }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone( String phone ) {
        this.phoneNumber = phone;
    }

    public void setPassword(String inputPassword) {
        this.password = inputPassword;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {return username; }

    public String getPassword() {
        return this.password;
    }

    public boolean hasNotifications() {
        return false;
    }
}
