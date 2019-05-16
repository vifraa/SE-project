package com.chalmers.gyarados.split;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Review;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter {

    private List<Review> reviewList;

    public ReviewListAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review,viewGroup,false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Review review = reviewList.get(position);
        ((ReviewHolder)viewHolder).bind(review);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }


    private class ReviewHolder extends RecyclerView.ViewHolder {
        private final TextView comment;
        private RatingBar ratingBar;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            comment = itemView.findViewById(R.id.comment);
        }

        void bind(Review review) {
            ratingBar.setRating(getNumberOfStars(review.getStars()));
            comment.setText(review.getComment());


        }

        private int getNumberOfStars(Review.Stars stars) {
            switch (stars){
                case ONE:return 1;
                case TWO: return 2;
                case THREE: return 3;
                case FOUR: return 4;
                case FIVE: return 5;
            }
            return -1;
        }
    }
    }
