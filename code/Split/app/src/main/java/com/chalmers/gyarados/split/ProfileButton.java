package com.chalmers.gyarados.split;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chalmers.gyarados.split.model.User;

public class ProfileButton extends AppCompatImageButton {

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
        this.user=givenUser;
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if(user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()){
            setImageURI(Uri.parse(user.getPhotoUrl()));
        }else{
            setImageResource(R.mipmap.ic_launcher);
        }

    }

    public User getUser() {
        return user;
    }
}
