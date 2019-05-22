package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.chalmers.gyarados.split.model.Review;
import com.chalmers.gyarados.split.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RatingActivity extends AppCompatActivity implements ReviewHolderListner {
    private Button rateConfirmButton;
    private RecyclerView ratingRecyclerView;
    private ImageButton leaveRateButton;
    private String groupID;
    private RestClient client;
    private RatingAdapter ratingAdapter;
    private List<User> users;
    private List<User> groupMembers;
    private CurrentSession session;
    private String myID;

    private Map<String,Review> reviewMap;

    //ratingRecyclerView.addView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_view);
        reviewMap=new HashMap<String,Review>();
        myID = session.getCurrentUser().getUserId();
        groupID = getIntent().getStringExtra("GroupID");
        groupMembers=new ArrayList<>();
        RestClient.getInstance().getGroupRepository().getGroup(groupID)
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myData -> {
                    if (myData != null) {
                        users = myData.getUsers();
                        for (User u: users) {
                            if (!u.getUserId().equals(CurrentSession.getCurrentUser().getUserId())) {
                                reviewMap.put(u.getUserId(), new Review(u));
                                groupMembers.add(u);
                            }
                        };
                        if(groupMembers.isEmpty()) {
                            goToNextActivity();
                        }
                        else {
                            initRatingView(groupMembers);
                        }
                    }
                }, throwable -> {
                        Log.d("hej", throwable.toString());

                        //showStatus("");
                    });

        rateConfirmButton = findViewById(R.id.confirmRateButton);
        disableConfirmButton();
        leaveRateButton = findViewById(R.id.leaveRateButton);

        rateConfirmButton.setOnClickListener(v -> {
            for (Map.Entry<String,Review> entry:reviewMap.entrySet()){
                Review review =entry.getValue();
                if(review.getComment()!=null || review.getStars()!=null){
                    RestClient.getInstance().getUserRepository().giveReview(entry.getKey(),review).unsubscribeOn(Schedulers.newThread())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((d)->{
                            }, throwable -> {
                                Log.d("Throwable",throwable.getMessage());
                            });
                }
                //review.setFloatStars(numStars);
                //review.setComment(feedbackComment);

            }
            goToNextActivity();
        });

        leaveRateButton.setOnClickListener(v -> {
            goToNextActivity();
        });
    }

    private void goToNextActivity() {
        Intent intent = new Intent(RatingActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void disableConfirmButton() {

        rateConfirmButton.setEnabled(false);
        rateConfirmButton.setBackgroundResource(R.drawable.disabled_button);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
        rateConfirmButton.setBackgroundResource(R.drawable.confirm_button);
    }

    public void initRatingView(List<User> userList) {
        if(userList.size() < 1) {

        }
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);
        ratingAdapter = new RatingAdapter(this, userList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ratingRecyclerView.setLayoutManager(linearLayoutManager);
        ratingRecyclerView.setAdapter(ratingAdapter);
    }

    @Override
    public void feedbackRecieved(String userID, String feedbackComment) {
        reviewMap.get(userID).setComment(feedbackComment);

    }

    @Override
    public void ratingRecieved(String userID, float rating) {
        reviewMap.get(userID).setFloatStars(rating);
        enableConfirmButton();
    }
}