package comcmput301f16t01.github.carrier.Users;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Creates a save file to tie a user's phone to their account after creating or logging in
 * on the phone.
 */
public class LoginMemory {
    Context saveContext;
    final String FILENAME = "LoginMemory.sav";

    public LoginMemory( Context ctx ) {
        saveContext = ctx;
    }

    /**
     * Saves a username to internal storage.
     * @param username The username you would like to save to internal storage.
     */
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

    /**
     * Loads a username from internal storage.
     * @return the username it loaded, or null if it could not find a username.
     */
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
}
