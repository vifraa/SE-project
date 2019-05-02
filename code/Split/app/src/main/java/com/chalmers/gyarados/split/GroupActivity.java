package com.chalmers.gyarados.split;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


import ua.naiksoftware.stomp.Stomp;

import ua.naiksoftware.stomp.StompClient;

public class GroupActivity extends AppCompatActivity {

    //-------------------------WEBSOCKET-ADDRESSES-------------------------
    private static final String ASK_FOR_GROUP_NUMBER = "/ws/find-group";
    private static final String RECEIVE_GROUP_NUMBER = "/user/queue/find-group";
    private static final String CHAT_PREFIX = "/ws/chat/";
    private static final String CHAT_ADD_USER_SUFFIX = "/addUser";
    private static final String CHAT_SEND_MESSAGE_SUFFIX = "/sendMessage";
    private static final String CHAT_ASK_FOR_GROUP_INFO = "/getInfo";
    private static final String CHAT_LEAVING_GROUP_SUFFIX = "/leave";


    //-------------------------CLIENT STUFF-----------------------------
    /**
     * The object we use to communicate with the server
     */
    private StompClient mStompClient;

    /**
     * Removes our subscriptions when we don't want to be subscribed to message/topic anymore
     */
    private CompositeDisposable compositeDisposable;

    //-------------CONFIG VALUES---------------------------
    /**
     * Used for logging
     */
    private static final String TAG = "ClientActivity";

    /**
     * The users name
     */
    private static final String NAME = "Tobias";

    /**
     * The group the server gives to me
     */
    private String myGroup;

    /**
     * The ip we want to connect to, given by activity before
     */
    private String ip;


    //------------------GUI-------------------------------

    /**
     * Used to show received messages
     */
    private TextView receivedMessages;
    /**
     * Where the user can write his messages
     */
    private EditText writtenText;
    /**
     * Used th show an loading animation while finding a group
     */
    private ViewDialog viewDialog;

    //------------------OTHER PROPERTIES------------------------------------
    /**
     * An object that helps us creating messages in json format
     */
    private JSONHelper jsonHelper;




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
        ip=getIntent().getStringExtra("IP");


        //initializing gui
        receivedMessages = findViewById(R.id.receivedMessages);
        writtenText = findViewById(R.id.writtenText);
        ImageButton sendButton = findViewById(R.id.sendbutton);
        ImageButton leaveButton = findViewById(R.id.leaveButton);
        sendButton.setOnClickListener(v -> onSendButtonPressed(writtenText.getText().toString()));
        leaveButton.setOnClickListener(l -> onLeaveButtonPressed());


        compositeDisposable = new CompositeDisposable();
        jsonHelper=new JSONHelper();

        viewDialog = new ViewDialog(this);
        showCustomLoadingDialog();


        //Connecting to server
        connectStomp();
    }
    /**
     * When the activity is to be destroyed the client will disconnect from the server.
     * We will also dispose all our current subscriptions.
     */
    @Override
    protected void onDestroy() {
        mStompClient.disconnect();

        if (compositeDisposable != null) compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    //-------------RECEIVING MESSAGE-------------------------------
    /**
     * This method is called when the user receives a new message that belongs to the group
     * @param messageInJson The new message, in json format
     */
    private void newGroupMessageReceived(String messageInJson) {
        hideCustomDialogIfNeeded();
        receivedMessages.setText(messageInJson);
    }

    /**
     * This method is called when the user receives a new message that belongs to the group
     * @param groupInfoInJson The group info, in json format
     */
    private void newGroupInfoReceived(String groupInfoInJson) {
        receivedMessages.setText(groupInfoInJson);
        Log.d(TAG,"newGroupInfoReceived");
    }

    //-------------SENDING MESSAGE------------------------------
    /**
     * Creates a json string with the given values. The string represents a find group message.
     * @return a json string
     */
    private String createFindGroupMessage() {
        return jsonHelper.createFindGroupMessage(NAME,57.684027,11.975490,57.735473, 12.112732);
    }

    /**
     * Creates a json string with the given values that represents a chat message
     * @param sender The sender of message
     * @param content The content of the message
     * @param type The type of the message: CHAT, JOIN or LEAVE
     * @return a json string
     */
    public String createChatMessage(String sender, String content, String type){
        return jsonHelper.createChatMessage(sender,content,type);
    }


    /**
     * Sending the given data to the given destination
     * @param destination The path we want to send to
     * @param data The message we want to send
     */
    private void sendMessage(String destination, String data){
        if(data!=null){
            compositeDisposable.add(mStompClient.send(destination, data)
                    .compose(applySchedulers()).subscribe(()
                                    -> Log.d(TAG, "Message send successfully"),
                            throwable -> errorWhileSendingMessage(throwable)));
        }else{
            Log.d(TAG,"Didn't send message since it was null");
        }

    }

    /**
     * Sends leave message to server on main thread
     * @param destination The path we want to send to
     */
    private void sendLeaveMessage(String destination){
        compositeDisposable.add(mStompClient.send(destination,jsonHelper.createChatMessage(NAME,null,"LEAVE"))
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()
                -> Log.d(TAG, "Leave message send successfully"),
                throwable -> errorWhileSendingMessage(throwable)));

    }

    //-----------------LEAVING----------------------------------------
    
    private void leaveGroup(){
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        if(mStompClient.isConnected()) {
            sendLeaveMessage(CHAT_PREFIX + myGroup + CHAT_LEAVING_GROUP_SUFFIX);
            mStompClient.disconnect();
        }

        finish();


        
        
        
    }

    //-----------------GUI METHODS----------------------------------

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
        if(message!=null && !message.isEmpty() && myGroup!=null){
            sendMessage(CHAT_PREFIX+ myGroup+ CHAT_SEND_MESSAGE_SUFFIX, createChatMessage(NAME,message,"CHAT"));
        }
        //todo gui stuff
    }

    public void onLeaveButtonPressed(){
        leaveGroup();
        //todo gui stuff
    }
    //-------------WEBSOCKET---------------------------------------

    /**
     * Connects to the server.
     * Subscribes on a topic and sends a join message.
     */
    private void connectStomp(){

        //Which ip-adress we want to connect to
        String uri= "ws://"+ip+"/split/websocket";
        Log.d(TAG,uri);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, uri );

        //We want to subscribe on a topic to be able to receive our group number
        Disposable dispTopic = mStompClient.topic(RECEIVE_GROUP_NUMBER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage ->{
                    //todo we need to check if the received message is ok
                    JsonObject message = jsonHelper.stringToJSONObject(topicMessage.getPayload());

                    myGroup = message.get("groupId").getAsString();

                    //We want to subscribe to messages on our given group
                    createRecevingMessageSubscription("/topic/"+myGroup);
                    //We want to subscribe on group info
                    createRecevingGroupInfoSubscription("/user/queue/getInfo/"+myGroup);
                    //We want to join our given group
                    sendMessage(CHAT_PREFIX+myGroup+ CHAT_ADD_USER_SUFFIX, createChatMessage(NAME,null,"JOIN"));
                    //We want to send a message to receive group info
                    sendMessage(CHAT_PREFIX+myGroup+CHAT_ASK_FOR_GROUP_INFO,createChatMessage(NAME,null,null));
                }, throwable -> {
                    errorOnSubcribingOnTopic(throwable);

                });

        compositeDisposable.add(dispTopic);

        //We want to ask the server for a group number.
        sendMessage(ASK_FOR_GROUP_NUMBER,createFindGroupMessage());


        mStompClient.connect();



        //Subscribes on the connection status

        Disposable dispLifecycle = mStompClient.lifecycle().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG,"Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG,"Stomp connection closed");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.d(TAG,"Stomp failed server heartbeat");
                            break;
                    }
                }, throwable -> {
                    errorOnLifeCycle(throwable);

                });

        compositeDisposable.add(dispLifecycle);


    }


    /**
     * Subscribing on the given destination
     * @param destination The destination we are trying to subscribe on
     */
    private void createRecevingMessageSubscription(String destination){
        Disposable dispTopic = mStompClient.topic(destination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tm -> newGroupMessageReceived(tm.getPayload()),
                        throwable -> errorOnSubcribingOnTopic(throwable));


        compositeDisposable.add(dispTopic);
    }

    private void createRecevingGroupInfoSubscription(String destination){
        Disposable dispTopic = mStompClient.topic(destination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tm -> newGroupInfoReceived(tm.getPayload()),
                        throwable -> errorOnSubcribingOnTopic(throwable));


        compositeDisposable.add(dispTopic);
    }




    /**
     * An object that ensures that a completable (like an observable) will be subscribed on and unsubscribed from on background threads
     * and observed on the main thread.
     * @return An object used by the compose operator to turn a Completable into another
     *  * Completable fluently.
     */
    private CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //-------------------------ERROR HANDLING------------------------------

    private void errorWhileSendingMessage(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error while sending message", throwable);
    }
    private void errorOnSubcribingOnTopic(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error on subscribe topic", throwable);
    }
    private void errorOnLifeCycle(Throwable throwable) {
        hideCustomDialogIfNeeded();
        Log.e(TAG, "Error on subscribe lifestyle", throwable);
    }





}
