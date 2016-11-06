package comcmput301f16t01.github.carrier;

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

public class UsernameTextView extends TextView {
    User user;

    public UsernameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setUser(User userToLink) {
        this.user = userToLink;

        this.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                String username = user.getUsername();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    UsernameTextView.this.setBackgroundColor(ContextCompat.getColor(
                            getContext(), R.color.usernameClick));
                    // When the name is released, go to user profile
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    UsernameTextView.this.setBackgroundColor(Color.TRANSPARENT);
                    Toast.makeText(getContext(), username, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

    }

    public void toProfile(User userToProfile) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        Bundle bundle = new Bundle();


    }
}
