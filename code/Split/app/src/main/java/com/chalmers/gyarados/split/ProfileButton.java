package com.chalmers.gyarados.split;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chalmers.gyarados.split.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileButton extends AppCompatImageButton {

    private static final int IMAGE_SIZE = 150;
    private User user;


    public ProfileButton(Context context, User user) {
        super(context);
        init(user);
    }

    public ProfileButton(Context context, AttributeSet attrs, User user) {
        super(context, attrs);
        init(user);
    }

    public ProfileButton(Context context, AttributeSet attrs, int defStyleAttr, User user) {
        super(context, attrs, defStyleAttr);
        init(user);
    }

    private void init(User givenUser){
        setPadding(0,0,20,0);
        this.user=givenUser;
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()){
            Picasso.with(getContext()).load(user.getPhotoUrl()).resize(IMAGE_SIZE,IMAGE_SIZE).into(this);

            //setImageURI(Uri.parse(user.getPhotoUrl()));
        }else{
            setImageResource(R.mipmap.ic_launcher);
        }

    }

    public User getUser() {
        return user;
    }
}
