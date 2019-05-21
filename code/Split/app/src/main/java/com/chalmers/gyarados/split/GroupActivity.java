package com.chalmers.gyarados.split;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class GroupActivity extends AppCompatActivity implements ClientListener, ProfileFragment.OnFragmentInteractionListener {

    //-------------LOGGING---------------------------
    /**
     * Used for logging
     */
    private static final String TAG = "ClientActivity";


    //------------------GUI-------------------------------
    /**
     * Used to show messages
     */
    private RecyclerView mMessageRecycler;
    /**
     * Used by the message recycler
     */
    private MessageListAdapter mMessageAdapter;

    /**
     * Where the user can write his messages
     */
    private EditText writtenText;
    /**
     * Used th show an loading animation while finding a group
     */
    private ViewDialog viewDialog;

    /**
     * Contains the profilebuttons
     */
    private LinearLayout buttonHolder;



    private TextView connection_status_textview;
    //------------------OTHER PROPERTIES------------------------------------

    /**
     * USed to connect to server and send messages
     */
    private Client client;


    private ImageButton sendButton;
    private ImageButton leaveButton;

    private boolean fromMainActivity;

    //-------------------ANDROID METHODS---------------------------------------------
    /**
     * The method that creates the activity.
     * Properties are created, gui elements are initialized and we try to establish a connection to the websocket.
     * @param savedInstanceState not used
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_view);

        //INITIALIZING GUI
        buttonHolder = findViewById(R.id.button_holder);
        connection_status_textview = findViewById(R.id.connection_status_textview);
        writtenText = findViewById(R.id.writtenText);
        sendButton = findViewById(R.id.sendbutton);
        leaveButton = findViewById(R.id.leaveButton);
        sendButton.setOnClickListener(v -> onSendButtonPressed(writtenText.getText().toString()));
        leaveButton.setOnClickListener(l -> onLeaveButtonPressed());
        viewDialog = new ViewDialog(this);
        showCustomLoadingDialog();

        //Retrieving the groupID that might have been given by activity before
        String groupID = getIntent().getStringExtra("groupID");
        if(groupID !=null){
            client=new Client(groupID,this);
        }else{
            fromMainActivity=true;
            client = new Client(this);
        }

        //CONNECT CLIENT
        client.connectStomp();
    }

    /**
     * When the activity is to be destroyed the client will disconnect from the server.
     * We will also dispose all our current subscriptions.
     */
    @Override
    protected void onDestroy() {
        client.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    //-------------RECEIVING MESSAGE-------------------------------
    /**
     * This method is called when the user receives a new message that belongs to the group
     * @param message a message
     */
    public void newGroupMessageReceived(Message message) {
        hideCustomDialogIfNeeded();
        if(message.getType().equals(MessageType.JOIN )){
            client.askForUserInfo(message.getSender());
        }else if(message.getType().equals(MessageType.LEAVE)){
            removeProfileButton(message.getSender());
        }
        mMessageAdapter.addItem(message);

        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()-1);
    }



    //-------------SENDING MESSAGE------------------------------


    private void sendChatMessage(Message message){
        client.sendMessageToChat(message);
    }



    //-----------------LEAVING----------------------------------------
    
    private void transferToRatingView(){
        client.leaveGroup();
        Intent intent = new Intent(GroupActivity.this,RatingActivity.class);
        intent.putExtra("GroupID", client.getGroupId());
        startActivity(intent);
        finish();
    }

    //-----------------GUI METHODS----------------------------------

    /**
     * Initialises the message recycler and the message adapter. Adds the given messages to the adapter.
     * @param messages The messages that are to be added to the message view.
     */
    private void initMessageView(List<Message> messages) {
        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messages);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(manager);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }
    private void hideCustomDialogIfNeeded() {
        if(viewDialog.isShowing()){
            hideCustomLoadingDialog();
        }
    }

    public void showCustomLoadingDialog() {
        //..show gif
        viewDialog.showDialog();
    }

    public void hideCustomLoadingDialog(){
        viewDialog.hideDialog();
    }
    /**
     * Sends a message to the earlier initialized group number.
     * @param message The message that are to be sent to the server
     */
    public void onSendButtonPressed(String message){
        if(message!=null && !message.isEmpty()){
            sendChatMessage(new Message(message,CurrentSession.getCurrentUser(),MessageType.CHAT));
            clearSendText();
        }
    }

    private void clearSendText() {
        writtenText.getText().clear();
    }

    public void onLeaveButtonPressed(){
        transferToRatingView();
    }

    public void updateMembersList(List<User> users) {
        for(User u:users){
            client.askForUserInfo(u);
        }
    }

    private void addProfileButton(User u) {
        ProfileButton button = new ProfileButton(getApplicationContext(),null,u);
        buttonHolder.addView(button);
        button.setOnClickListener(new ProfileClickListener(u));
    }

    private void removeProfileButton(User user) {
        int size = buttonHolder.getChildCount();
        for(int i = 0; i<size;i++){
            ProfileButton b = (ProfileButton)buttonHolder.getChildAt(i);
            if (b.getUser().getUserId().equals(user.getUserId())){
                buttonHolder.removeViewAt(i);
                break;
            }
        }

    }

    private class ProfileClickListener implements View.OnClickListener {
        private User user;

        ProfileClickListener(User user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            ProfileFragment fragment = ProfileFragment.newInstance();
            fragment.setUser(user);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragmentholder, fragment).commit();
            findViewById(R.id.fragmentholder).bringToFront();
        }
    }
    //-------------INITIALIZING---------------------------------------


    /**
     * Called when client has found a group and received all old messages
     * @param messages a list with messages
     */
    @Override
    public void onOldMessagesReceived(List<Message> messages) {
        initMessageView(messages);
    }

    /**
     * Called when the client is ready.
     */
    @Override
    public void onClientReady() {
        hideCustomDialogIfNeeded();
    }

    /**
     * Called when info about a user is received.
     * Update the profile button!
     * @param user the user we have received information about
     */
    @Override
    public void userInfoReceived(User user) {
        addProfileButton(user);
    }



    //-------------------------ERROR HANDLING------------------------------

    /**
     * If there is some kind of error on the clients lifecycle, lets just go back to previous activity
     */
    @Override
    public void errorOnLifeCycleFirstConnect() {
        hideCustomDialogIfNeeded();
        client.disconnect();
        returnToPreviousActivity();
    }

    /**
     * If we cant establish a connection on the first try, lets just go back to previous activity
     */
    @Override
    public void onConnectionClosedFirstConnect() {
        hideCustomDialogIfNeeded();
        client.disconnect();
        returnToPreviousActivity();
    }

    /**
     * When the connection between the server and client closes.
     * Tries to reconnect.
     */
    @Override
    public void onConnectionClosed() {
        disableActionsOnDisconnect();
        tryToReconnect();

    }

    private void tryToReconnect(){
        //Show status bar
        connection_status_textview.setVisibility(View.VISIBLE);
        new ReconnectCountDown(2L, TimeUnit.SECONDS).start();
    }


    /**
     * When the client has managed to open a connection to the server
     */
    @Override
    public void onConnectionOpened() {
        //Do nothing right now
    }


    /**
     * When the reconnection fails
     */
    @Override
    public void onReConnectingFailed() {
        //Just try again and again every 10 seconds...
        new ReconnectCountDown(10L, TimeUnit.SECONDS).start();
    }


    /**
     * When the reconnection is successful
     */
    @Override
    public void onReconnectingSuccess() {
        connection_status_textview.setVisibility(View.GONE);
        enableActionsOnConnect();
        client.askForMessagesAfter(mMessageAdapter.getLastMessageTimestamp());
    }

    /**
     * Handles the messages sent to the group while disconnected
     * @param messages a list with messages
     */
    @Override
    public void onMessagesReceivedWhenDisconnected(List<Message> messages) {
        for (Message m:messages){
            mMessageAdapter.addItem(m);
        }
    }

    /**
     * If there is an error when sending a message...
     * @param throwable the error
     */
    public void errorWhileSendingMessage(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error while sending message", throwable);
        //todo what if this happens...

    }

    /**
     * Handles the event that the client fails to subscribe on a topic
     * @param throwable The error
     */
    public void errorOnSubcribingOnTopic(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error on subscribe topic", throwable);
        //todo what if this happens...


    }

    /**
     * If there is some kind of error on the client lifecycle, this will be called
     */
    public void errorOnLifeCycle() {
        Log.e(TAG, "Lifecycle error");
        //todo what if this happens...
    }


    /**
     * Returns to previous activity.
     */
    private void returnToPreviousActivity(){
        if(fromMainActivity){
            finish();
        }else{
            Intent intent = new Intent(GroupActivity.this,MainActivity.class);
            finish();
            startActivity(intent);
        }

    }

    /**
     *Disables the possibility to leave group and sending messages
     */
    private void disableActionsOnDisconnect(){
        leaveButton.setEnabled(false);
        sendButton.setEnabled(false);
    }

    /**
     *Enables the possibility to leave group and sending messages
     */
    private void enableActionsOnConnect(){
        leaveButton.setEnabled(true);
        sendButton.setEnabled(true);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A class that takes care of counting down when trying to reconnect.
     */
    private class ReconnectCountDown extends CountDownTimer{
        ReconnectCountDown(Long startValue, TimeUnit timeUnit) {
            super(startValue, timeUnit);
        }

        @Override
        public void onTick(long tickValue) {
            String toShow = getString(R.string.trying_to_reconnect) + (int)tickValue;
            connection_status_textview.setText(toShow);
        }

        @Override
        public void onFinish() {
            String toShow = getString(R.string.reconnecting);
            connection_status_textview.setText(toShow);
            client.tryToReconnect();
        }
    }
}
