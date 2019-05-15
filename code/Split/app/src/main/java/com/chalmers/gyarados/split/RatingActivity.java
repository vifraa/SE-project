package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Review;
import com.chalmers.gyarados.split.model.User;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RatingActivity extends AppCompatActivity  {
    private Button rateConfirmButton;
    private RecyclerView ratingRecyclerView;
    private ImageButton leaveRateButton;
    private RatingBar ratingBar;
    private TextView feedbackText;
    private float numStars;
    private String feedbackComment;
    private Review review;
    private String groupID;
    private RestClient client;
    private RatingAdapter ratingAdapter;
    private List<User> users;
    private List<User> groupMembers;
    private CurrentSession session;
    private String myID;

    //ratingRecyclerView.addView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myID = session.getCurrentUser().getUserId();
        rateConfirmButton = findViewById(R.id.confirmButton);
        disableConfirmButton();
        leaveRateButton = findViewById(R.id.leaveRateButton);
        ratingBar = findViewById(R.id.ratingBar);
        feedbackText.findViewById(R.id.feedbackCommentTextField);
        groupID = getIntent().getStringExtra("GroupID");
        client.getInstance().getGroupRepository().getGroup(groupID)
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myData -> {
                    if (myData != null) {
                        users = myData.getUsers();
                        for (User u: users) {
                            if (u.getUserId() != myID)
                                groupMembers.add(u);
                        }
                        initRatingView(groupMembers);
                    };
                }, throwable -> {
                        //Log.d(TAG, throwable.toString());

                        //showStatus("");
                    });

        rateConfirmButton.setOnClickListener(v -> {
            for (User g: groupMembers){
                RestClient.getInstance().getUserRepository().giveReview(g.getUserId(),review);
            }
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
            review.setReviewMsg(feedbackComment);
        });

        ratingBar.setOnClickListener(v-> {
            enableConfirmButton();
            numStars = ratingBar.getRating();
            review.setFloatStars(numStars);

        });
    }

    public void disableConfirmButton() {
        rateConfirmButton.setEnabled(false);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
    }

    public void initRatingView(List<User> userList) {
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);
        ratingAdapter = new RatingAdapter(this,userList);
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingRecyclerView.setAdapter(ratingAdapter);
    }

}