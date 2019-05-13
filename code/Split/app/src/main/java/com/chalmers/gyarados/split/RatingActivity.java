package com.chalmers.gyarados.split;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageButton;

import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import java.util.List;

public class RatingActivity extends AppCompatActivity implements ClientListener {
    private Button rateConfirmButton;
    private RecyclerView ratingRecyclerView;
    private ImageButton leaveRateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        rateConfirmButton = findViewById(R.id.confirmButton);
        disableConfirmButton();
        leaveRateButton = findViewById(R.id.leaveRateButton);
        ratingRecyclerView = findViewById(R.id.rating_recycler_view);

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

        super.onCreate(savedInstanceState);
    }

    public void disableConfirmButton() {
        rateConfirmButton.setEnabled(false);
    }

    private void enableConfirmButton() {
        rateConfirmButton.setEnabled(true);
    }

    @Override
    public void updateMembersList(List<User> users) {

    }

    @Override
    public void newGroupMessageReceived(Message message) {

    }

    @Override
    public void errorOnSubcribingOnTopic(Throwable throwable) {

    }

    @Override
    public void errorOnLifeCycle(Throwable throwable) {

    }

    @Override
    public void errorWhileSendingMessage(Throwable throwable) {

    }

    @Override
    public void onOldMessagesReceived(List<Message> messages) {

    }

    @Override
    public void onClientReady() {

    }

}