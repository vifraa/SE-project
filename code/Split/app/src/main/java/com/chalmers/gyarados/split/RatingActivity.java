package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.Review;
import com.chalmers.gyarados.split.model.User;

import java.util.List;

public class RatingActivity extends AppCompatActivity  {
    private Button rateConfirmButton;
    private RecyclerView ratingRecyclerView;
    private ImageButton leaveRateButton;
    private RatingBar ratingBar;
    private TextView feedbackText;
    private int numStars;
    private String feedbackComment;
    private Review review;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rateConfirmButton = findViewById(R.id.confirmButton);
        disableConfirmButton();
        leaveRateButton = findViewById(R.id.leaveRateButton);
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);
        ratingBar = findViewById(R.id.ratingBar);
        feedbackText.findViewById(R.id.feedbackCommentTextField);
        //RestClient.getInstance().getGroupRepository().getGroup();
        //RestClient.getInstance().getUserRepository().giveReview();

        rateConfirmButton.setOnClickListener(v -> {
            Intent intent = new Intent(RatingActivity.this,MainActivity.class);
            startActivity(intent);
        });

        leaveRateButton.setOnClickListener(v -> {
            Intent intent = new Intent(RatingActivity.this,MainActivity.class);
            startActivity(intent);
        });

        ratingRecyclerView.setOnClickListener(v -> {
            Intent intent = new Intent(RatingActivity.this,MainActivity.class);
            startActivity(intent);
        });

        feedbackText.setOnClickListener(v -> {
            feedbackComment = feedbackText.getText().toString();
        });

        ratingBar.setOnClickListener(v-> {
            enableConfirmButton();
            numStars = ratingBar.getNumStars();
        });
    }

    public void disableConfirmButton() {
        rateConfirmButton.setEnabled(false);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
    }

}