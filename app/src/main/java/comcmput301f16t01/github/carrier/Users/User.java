package comcmput301f16t01.github.carrier.Users;

import io.searchbox.annotations.JestId;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Abstract base class for a user of Carrier.
 *
 * @see User
 * @see User
 */

public class User implements Parcelable{
    private String username;
    private String email;
    private String phoneNumber;
    private String vehicleDescription;

    /**
     * For use with Elastic Search, is the unique ID given to it
     */
    @JestId
    private String elasticID;

    //TODO we should probably say what is and isn't a valid username, email, and phone number.

    /**
     * Constructor, requires username, email, and phone number.
     *
     * @param inputUsername             The username
     * @param inputEmail                The e-mail
     * @param inputPhoneNumber          The phone number
     * @param inputvehicleDescription   The vehicle information
     */
    public User(@NonNull String inputUsername, @NonNull String inputEmail, @NonNull String inputPhoneNumber, @NonNull String inputvehicleDescription) {
        this.username = inputUsername;
        this.email = inputEmail;
        this.phoneNumber = inputPhoneNumber;
        this.vehicleDescription = inputvehicleDescription;
    }

    public User() {
        this.username = "default_name";
        // TODO this method was implemented to create a default method for extending classes. Probably needs refactoring.
    }

    public User(String name) {
        this.username = name;
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        vehicleDescription = in.readString();
        elasticID = in.readString();
    }

    /**
     * Required by the Parcelable interface. Allows the creation and storage of parcels in bundles.
     */
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
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

    @Override
    /**
     * Required by the Parcelable interface. I honestly don't know what this is for.
     */
    public int describeContents() {
        return 0;
    }

    @Override
    /**
     * Required by the Parcelable interface.
     * Allows the User class to be put into a bundle using putParcelable() on a bundle.
     * @param dest
     * @param flags
     */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(vehicleDescription);
        dest.writeString(elasticID);
    }
}