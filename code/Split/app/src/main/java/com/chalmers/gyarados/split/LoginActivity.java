package com.chalmers.gyarados.split;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.internal.LinkedTreeMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private Disposable mRestPingDisposable;

    private GoogleSignInClient signInClient;

    private ViewDialog viewDialog;

    private TextView statusTextView;

    private SignInButton signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        disableLoginButton();


        viewDialog = new ViewDialog(this);

        statusTextView = findViewById(R.id.onErrorTextView);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    protected void onDestroy() {
        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        super.onDestroy();

    }

    private User createUser(GoogleSignInAccount acct) {
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personId = acct.getId();
            Uri photo = acct.getPhotoUrl();
            return new User(personName, personId, photo);
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {
            showCustomLoadingDialog();
            showStatus("Connecting to server...");
            User user = createUser(account);
            tryToStartApplication(user);

        } else {
            enableLoginButton();
            // do nothing
        }

    }

    private void tryToStartApplication(User user) {
        mRestPingDisposable = RestClient.getInstance().getExampleRepository().sendRestEcho(user)
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myData -> {
                    if(myData!=null){
                        LinkedTreeMap map = (LinkedTreeMap)myData.get("user");
                        boolean hasGroup = (boolean)myData.get("hasGroup");
                        if(map!=null){
                            User givenUser = new User(map);
                            CurrentSession.setCurrentUser(givenUser);
                            if(!hasGroup){
                                startMainActivity();
                            }else{
                                String groupID=(String)myData.get("groupID");
                                if(groupID!=null){
                                    startGroupActivity(groupID);
                                }

                            }
                        }else{
                            showStatus("Couldn't fetch data from");
                            enableLoginButton();
                        }
                    }else{
                        showStatus("Couldn't fetch data from");
                        enableLoginButton();
                    }

                    hideCustomDialogIfNeeded();


                }, throwable -> {
                    Log.d(TAG, throwable.toString());
                    hideCustomDialogIfNeeded();
                    enableLoginButton();
                    showStatus("Couldn't connect to server");
                });

    }

    private void startGroupActivity(String groupID) {
        Intent intent = new Intent(LoginActivity.this,GroupActivity.class);
        intent.putExtra("groupID",groupID);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                clearErrorText();
                signIn();
                break;
        }
    }


    private void showStatus(String error){
        statusTextView.setText(error);
    }

    private void clearErrorText(){
        statusTextView.setText(null);
    }

    private void hideCustomDialogIfNeeded() {
        if(viewDialog.isShowing()){
            hideCustomLoadingDialog();
        }
    }

    public void showCustomLoadingDialog() {
        //if(!viewDialog.isShowing()){
            viewDialog.showDialog();
        //}

    }

    public void hideCustomLoadingDialog(){
        viewDialog.hideDialog();
    }

    private void disableLoginButton(){
        signInButton.setEnabled(false);
    }

    private void enableLoginButton(){
        signInButton.setEnabled(true);
    }
}
