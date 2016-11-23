package comcmput301f16t01.github.carrier.Users;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import comcmput301f16t01.github.carrier.Users.User;
import comcmput301f16t01.github.carrier.Users.UserController;

/**
 * Creates a save file to tie a user's phone to their account after creating or logging in
 * on the phone.
 */
public class LoginMemory {
    Context saveContext;
    final String FILENAME = "LoginMemory.sav";
    final String USER_FILENAME = "User.sav";

    public LoginMemory( Context ctx ) {
        saveContext = ctx;
    }

    public void saveUsername( String username ) {
        try {
            FileOutputStream fos = saveContext.openFileOutput(FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            out.write(username);
            out.flush();

            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void saveUser(User userToCache) {
        try {
            FileOutputStream fos = saveContext.openFileOutput(USER_FILENAME, 0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            if (userToCache == null) {
                out.write("");
            } else {
                Gson gson = new Gson();
                gson.toJson(userToCache, out);
            }
            out.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadUsername() {
        String username;
        try {
            FileInputStream fis = saveContext.openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            username = in.readLine();

            in.close();
            fis.close();
        } catch (IOException e) {
            return null;
        }

        return username;
    }

    public User loadUser() {
        FileInputStream fis = null;
        User lastLoggedInUser = null;
        try {
            fis = saveContext.openFileInput(USER_FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type type = new TypeToken<User>() {}.getType();
            lastLoggedInUser = gson.fromJson(in, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lastLoggedInUser;
    }
}
