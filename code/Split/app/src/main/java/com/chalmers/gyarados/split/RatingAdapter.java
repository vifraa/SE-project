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

    private ReviewHolderListner adapterListener;


    public RatingAdapter (Context context, List<User> userList, ReviewHolderListner listner){
        this.context = context;
        this.userList = userList;
        this.adapterListener=listner;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feedback_person,viewGroup,false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        User user = userList.get(i);
        ReviewHolder reviewHolder = (ReviewHolder)viewHolder;
        reviewHolder.bind(user);
        reviewHolder.setListener(adapterListener);

    }

    private class ReviewHolder extends RecyclerView.ViewHolder {
        TextView feedbackTextView, feedbackName;
        ImageView imageRatingProfile;
        RatingBar ratingBar;
        private ReviewHolderListner listener;
        private User user;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            feedbackTextView = itemView.findViewById(R.id.feedbackCommentTextField);
            imageRatingProfile = itemView.findViewById(R.id.image_rating_profile);
            feedbackName = itemView.findViewById(R.id.feedback_name);
            ratingBar = itemView.findViewById(R.id.feedbackRatingBar);

            feedbackTextView.setOnClickListener(v -> {
                listener.feedbackRecieved(user.getUserId(), feedbackTextView.getText().toString());
            });
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    listener.ratingRecieved(user.getUserId(), ratingBar.getRating());
                }
            });

        }

        void bind(User user) {
            feedbackName.setText(user.getName());
            //imageRatingProfile.setImage(user.getPhotoUrl());
            this.user = user;
        }

        void setListener (ReviewHolderListner listener) {
            this.listener = listener;
        }
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void addItem(User user) {
        userList.add(user);
        notifyItemInserted(getItemCount()-1);
    }
}
