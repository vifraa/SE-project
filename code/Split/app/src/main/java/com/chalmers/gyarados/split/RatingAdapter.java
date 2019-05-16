package com.chalmers.gyarados.split;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        EditText feedbackTextView;
        TextView feedbackName;
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

            feedbackTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.feedbackRecieved(user.getUserId(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
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
            ImageConverter.loadRoundedImage(context,user.getPhotoUrl(),imageRatingProfile);
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
