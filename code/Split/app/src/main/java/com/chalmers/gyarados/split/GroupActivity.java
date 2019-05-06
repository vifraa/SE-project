package com.chalmers.gyarados.split;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import java.util.List;


public class GroupActivity extends AppCompatActivity implements ClientListener {

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

    private TextView groupMembers;

    //------------------OTHER PROPERTIES------------------------------------

    /**
     * USed to connect to server and send messages
     */
    private Client client;

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

        //Retrieving the ip-address given in activity before
        String ip=getIntent().getStringExtra("IP");


        //initializing gui
        writtenText = findViewById(R.id.writtenText);
        groupMembers=findViewById(R.id.groupMembers);
        ImageButton sendButton = findViewById(R.id.sendbutton);
        ImageButton leaveButton = findViewById(R.id.leaveButton);
        sendButton.setOnClickListener(v -> onSendButtonPressed(writtenText.getText().toString()));
        leaveButton.setOnClickListener(l -> onLeaveButtonPressed());

        viewDialog = new ViewDialog(this);
        showCustomLoadingDialog();

        client = new Client(ip,this);
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
     * @param message
     */
    public void newGroupMessageReceived(Message message) {
        hideCustomDialogIfNeeded();
        if(message.getType().equals(MessageType.JOIN )|| message.getType().equals(MessageType.LEAVE)){
            client.askForGroupInfo();
        }
        mMessageAdapter.addItem(message);

        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()-1);
        //receivedMessages.setText(messageInJson);
    }

    //-------------SENDING MESSAGE------------------------------


    private void sendChatMessage(Message message){
        client.sendMessageToChat(message);
    }



    //-----------------LEAVING----------------------------------------
    
    private void leaveGroup(){
        client.leaveGroup();
    }

    //-----------------GUI METHODS----------------------------------

    /**
     * Initialises the message recycler and the message adapter. Adds the given messages to the adapter.
     * @param messages The messages that are to be added to the message view.
     */
    private void initMessageView(List<Message> messages) {
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
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
        leaveGroup();
        returnToPreviousActivity();
    }

    public void updateMembersList(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for(User u:users){
            sb.append(u.getName());
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length()-1);
        groupMembers.setText(sb.toString());
    }
    //-------------INITIALIZING---------------------------------------


    /**
     * Called when client has found a group and received all old messages
     * @param messages
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




    //-------------------------ERROR HANDLING------------------------------

    public void errorWhileSendingMessage(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error while sending message", throwable);

    }


    public void errorOnSubcribingOnTopic(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error on subscribe topic", throwable);
        client.disconnect();
        returnToPreviousActivity();

    }
    public void errorOnLifeCycle(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error on subscribe lifestyle", throwable);
        client.disconnect();
        returnToPreviousActivity();
    }


    private void returnToPreviousActivity(){
        finish();
    }





}
