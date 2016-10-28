package comcmput301f16t01.github.carrier;

import java.util.ArrayList;

@Deprecated
public class UserList extends ArrayList{

    /**
     * Contains
     */
    ArrayList<User> users;

    public UserList() {
        users = new ArrayList<User>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

}
