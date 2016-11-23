package comcmput301f16t01.github.carrier.Users;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import comcmput301f16t01.github.carrier.R;

/**
 * This TextView is specifically used for usernames. It contains logic that highlights on touch
 * and redirects the user to the profile of the username that was touched.
 */
public class UsernameTextView extends TextView {
    User user;

    /**
     * Constructor
     * @param context Where the UsernameTextView is situated
     * @param attrs A list of attributes
     */
    public UsernameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the user that upon the click of the TextView, redirects to the set user's profile.
     * @param userToLink The username of the profile to view.
     */
    public void setUser(User userToLink) {
        this.user = userToLink;

        this.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                String username = user.getUsername();
                // When the name is being touched, highlight the background
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UsernameTextView.this.setBackgroundColor(ContextCompat.getColor(
                            getContext(), R.color.usernameClick));
                    // When the name is released, go to user profile and remove highlight
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    UsernameTextView.this.setBackgroundColor(Color.TRANSPARENT);
                    toProfile(user);
                    Toast.makeText(getContext(), username, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    /**
     * Creates an intent with a bundle that redirects to the touched username's profile.
     * @param userToProfile The username of the profile to view.
     */
    public void toProfile(User userToProfile) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        Bundle bundle = new Bundle();
        // packs the parcel with a user so that userprofile knows which to load.
        bundle.putParcelable("user", userToProfile);
        intent.putExtras(bundle);
        // onward!
        getContext().startActivity(intent);

    }
}
