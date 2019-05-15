package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;

public class RateActivity extends AppCompatActivity {

    Button confirmButton;
    ImageButton leaveRateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_view);

        confirmButton = findViewById(R.id.confirmButton);
        leaveRateButton = findViewById(R.id.leaveProfileButton);
    }

    public void leaveRateButtonPressed() {
        Intent intent = new Intent(RateActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
