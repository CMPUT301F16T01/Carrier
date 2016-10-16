package comcmput301f16t01.github.carrier;
/**
 * Abstract base class for a user of Carrier.
 *
 * @see Rider
 * @see Driver
 */
public abstract class User {
    protected String username;
    protected String password;
    protected String email;
    protected String phoneNumber;

    public User() {
        this.username = "default_name";
        // TODO this method was implemented to create a default method for extending classes. Probably needs refactoring.
    }

    public User( String name ) {
        this.username = name;
    }

    public void setUsername(String username) {this.username = username; }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone( String phone ) {
        this.phoneNumber = phone;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {return username; }
}
