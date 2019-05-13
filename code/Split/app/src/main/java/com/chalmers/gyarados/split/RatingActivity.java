package com.chalmers.gyarados.split;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageButton;

import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import java.util.List;

public class RatingActivity extends AppCompatActivity {
    private Button rateConfirmButton;
    private RecyclerView ratingRecyclerView;
    private ImageButton leaveRateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        rateConfirmButton = findViewById(R.id.confirmButton);
        leaveRateButton = findViewById(R.id.leaveRateButton);
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);

        disableConfirmButton();





        super.onCreate(savedInstanceState);
    }

    private void disableConfirmButton() {
        rateConfirmButton.setEnabled(false);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
    }
}
