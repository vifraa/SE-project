package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

public class ProfileActivity extends AppCompatActivity {

    ImageButton leaveProfileButton;

    boolean leftGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        leaveProfileButton = findViewById(R.id.leaveProfileButton);
    }

    public void leaveProfileButtonPressed() {
        returnToPreviousActivity();
    }

    private void returnToPreviousActivity() {
        if (leftGroup) {
            Intent intent = new Intent(ProfileActivity.this, GroupActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
