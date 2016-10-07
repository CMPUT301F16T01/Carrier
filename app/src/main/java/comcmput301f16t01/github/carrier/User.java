package comcmput301f16t01.github.carrier;
/**
 * Abstract base class for a user of Carrier.
 *
 * @see Rider
 * @see Driver
 */
public abstract class User {
    protected String username;

    public User() {
        this.username = "default_name";
        // TODO this method was implemented to create a default method for extending classes. Probably needs refactoring.
    }

    public User( String name ) {
        this.username = name;
    }

}
