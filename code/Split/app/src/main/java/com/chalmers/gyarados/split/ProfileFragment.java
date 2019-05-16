package com.chalmers.gyarados.split;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Review;
import com.chalmers.gyarados.split.model.User;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private User user;

    ImageButton leaveProfileButton;
    TextView headerName;
    TextView name;
    TextView age;
    TextView avgRating;
    TextView numOfRatings;
    ImageView profileImage;

    private RecyclerView mMessageRecycler;
    private ReviewListAdapter reviewListAdapter;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    public void setUser(User user) {
        this.user = user;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_view, container, false);
        leaveProfileButton = v.findViewById(R.id.leaveProfileButton);
        headerName = v.findViewById(R.id.profile_headername);
        name = v.findViewById(R.id.profile_username);
        //age = v.findViewById(R.id.profile_age);
        avgRating = v.findViewById(R.id.profile_avg_rating);
        numOfRatings = v.findViewById(R.id.profile_number_of_ratings);
        profileImage = v.findViewById(R.id.profile_image);

        leaveProfileButton.setOnClickListener(view -> getFragmentManager().beginTransaction()
                .remove(ProfileFragment.this).commit());


        headerName.setText(user.getName() + "'s profile");
        name.setText(user.getName());
        //age.setText("22");

        List<Review> userReviews = user.getReviews();
        if(userReviews==null){
            userReviews=new ArrayList<>();

        }
        double avgRatingNumber = calculateAverageReview(userReviews);

        if(avgRatingNumber!=-1){
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String avgText = "Average rating: " + decimalFormat.format(avgRatingNumber);
            avgRating.setText(avgText);
        }else{
            avgRating.setText("No average rating");
        }
        String nrOfReviews = "# of ratings: " + String.valueOf(userReviews.size());
        numOfRatings.setText(nrOfReviews);
        mMessageRecycler = (RecyclerView) v.findViewById(R.id.rating_recycler);
        reviewListAdapter = new ReviewListAdapter(userReviews);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mMessageRecycler.setAdapter(reviewListAdapter);


        //avgRating.setText(user.getAvgRating());
        //numOfRatings.setText(user.getNumOfRatings());
        if (user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            ImageConverter.loadRoundedImage(getContext(),user.getPhotoUrl(),profileImage);
        } else {
            profileImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.profile_pic_default, null));
        }




        return v;
    }

    private double calculateAverageReview(List<Review> userReviews) {
        if(userReviews.isEmpty()){
            return -1;
        }else{
            double total=0;
            for(Review r:userReviews){
                total+=getNumberOfStars(r.getStars());
            }


            return (total/userReviews.size());
        }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
