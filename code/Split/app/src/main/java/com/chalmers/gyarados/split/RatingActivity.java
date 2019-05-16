package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

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
                            if (u.getUserId().equals(CurrentSession.getCurrentUser().getUserId())) {
                                reviewMap.put(u.getUserId(), new Review(u));
                                groupMembers.add(u);
                            }
                        };
                        initRatingView(groupMembers);
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
                if(review.getReviewMsg()!=null || review.getStars()!=null){
                    RestClient.getInstance().getUserRepository().giveReview(entry.getKey(),review).unsubscribeOn(Schedulers.newThread())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((d)->{
                            }, throwable -> {
                                Log.d("Throwable",throwable.getMessage());
                            });
                }
                //review.setFloatStars(numStars);
                //review.setReviewMsg(feedbackComment);

            }
            Intent intent = new Intent(RatingActivity.this,MainActivity.class);
            startActivity(intent);
        });

        leaveRateButton.setOnClickListener(v -> {
            Intent intent = new Intent(RatingActivity.this,MainActivity.class);
            startActivity(intent);
        });
    }

    private void addFeedbackCommentToUser(String feedbackComment, User user) {
    }

    private void addFeedbackRatingToUser() {
    }

    public void disableConfirmButton() {
        rateConfirmButton.setEnabled(false);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
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
        reviewMap.get(userID).setReviewMsg(feedbackComment);

    }

    @Override
    public void ratingRecieved(String userID, float rating) {
        reviewMap.get(userID).setFloatStars(rating);
        enableConfirmButton();
    }
}