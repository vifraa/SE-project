package com.chalmers.gyarados.split;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.User;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<User> userList;
    private ReviewHolder viewHolder;
    public RatingAdapter (Context context, List<User> userList){
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feedback_person,viewGroup);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        User user =userList.get(i);
        ((ReviewHolder)viewHolder).bind(user);

    }

    private class ReviewHolder extends RecyclerView.ViewHolder {
        TextView feedbackTextView, feedbackName;
        ImageView imageRatingProfile;
        RatingBar ratingBar;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackTextView = itemView.findViewById(R.id.feedbackCommentTextField);
            imageRatingProfile = itemView.findViewById(R.id.image_rating_profile);
            feedbackName = itemView.findViewById(R.id.feedback_name);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(User user) {
            feedbackName.setText(user.getName());
            //imageRatingProfile.setImage(user.getPhotoUrl());
        }
    }


    @Override
    public int getItemCount() {
        return 0;
    }
}
