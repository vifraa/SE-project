package com.chalmers.gyarados.split;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.chalmers.gyarados.split.model.User;

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
        age = v.findViewById(R.id.profile_age);
        avgRating = v.findViewById(R.id.profile_avg_rating);
        numOfRatings = v.findViewById(R.id.profile_number_of_ratings);
        profileImage = v.findViewById(R.id.profile_image);
        headerName.setText(user.getName() + "'s profile");
        name.setText(user.getName());
        age.setText("22");
        //avgRating.setText(user.getAvgRating());
        //numOfRatings.setText(user.getNumOfRatings());
        if (user.getProfileURL() != null && !user.getProfileURL().isEmpty()) {
            //profileImage.setImageURI(user.getProfileURL());
        } else {
            profileImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.profile_pic_default, null));
        }
        return v;
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
