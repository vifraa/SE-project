package com.chalmers.gyarados.split;

import android.util.Log;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class Client {

    //-------------------------WEBSOCKET-ADDRESSES-------------------------
    private static final String ASK_FOR_GROUP_NUMBER = "/ws/find-group";
    private static final String RECEIVE_GROUP_NUMBER = "/user/queue/find-group";
    private static final String CHAT_PREFIX = "/ws/chat/";
    private static final String CHAT_SEND_MESSAGE_SUFFIX = "/sendMessage";
    private static final String CHAT_ASK_FOR_GROUP_INFO = "/getInfo";
    private static final String CHAT_LEAVING_GROUP_SUFFIX = "/leave";


    /**
     * The object we use to communicate with the server
     */
    private StompClient mStompClient;

    /**
     * Removes our subscriptions when we don't want to be subscribed to message/topic anymore
     */
    private CompositeDisposable compositeDisposable;

    /**
     * Used for logging
     */
    private static final String TAG = "ClientActivity";

    /**
     * The group the server gives to me
     */

    /**
     * An object that helps us creating messages in json format
     */
    private JSONHelper jsonHelper;

    /**
     * An object that wants to receive information about what the client receives
     */
    private ClientListener clientListener;


    private String groupID;

    private boolean firstTime;


    public Client(String groupID, ClientListener clientListener) {
        this.groupID=groupID;
        this.clientListener = clientListener;
        compositeDisposable = new CompositeDisposable();
        jsonHelper=new JSONHelper();
    }

    public Client(ClientListener clientListener) {
        this.clientListener = clientListener;
        compositeDisposable = new CompositeDisposable();
        jsonHelper=new JSONHelper();
    }

    /**
     * Connects to the server.
     * Subscribes on a topic and sends a join message.
     */
    public void connectStomp(){

        String uri;
        //Which ip-adress we want to connect to
        if(Constants.develop){
            uri= "ws://"+Constants.IP+":"+Constants.PORT+"/split/websocket";
        }else{
            uri = "ws://"+Constants.deployedURL+"/split/websocket";
        }

        Log.d(TAG,uri);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, uri );


        if(groupID!=null){
            firstTime=true;
            subscribeOnGroup();
            askForGroupInfo();

        }else{
            //We want to subscribe on a topic to be able to receive our group number
            Disposable dispTopic = mStompClient.topic(RECEIVE_GROUP_NUMBER)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(topicMessage ->{
                        subscribeOnGroup();
                        //Now we have received a group
                        Group myGroup = jsonHelper.convertJsonToGroup(topicMessage.getPayload());
                        clientListener.updateMembersList(myGroup.getUsers());
                        groupID=myGroup.getId();

                        subscribeOnGroup();

                        clientListener.onOldMessagesReceived(myGroup.getMessages());
                        clientListener.onClientReady();


                    }, throwable -> {
                        clientListener.errorOnSubcribingOnTopic(throwable);

                    });

            compositeDisposable.add(dispTopic);

            //We want to ask the server for a group number.
            sendFindGroupMessage(createFindGroupMessage());
        }



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
                    clientListener.errorOnLifeCycle(throwable);

                });

        compositeDisposable.add(dispLifecycle);

    }



    private void subscribeOnGroup() {
        //We want to subscribe to messages on our given group
        createReceivingMessageSubscription("/topic/"+groupID);
        //We want to subscribe on group info
        createRecevingGroupInfoSubscription("/user/queue/getInfo/"+groupID);
    }


    /**
     * Subscribing on the given destination
     * @param destination The destination we are trying to subscribe on
     */
    private void createReceivingMessageSubscription(String destination){
        Disposable dispTopic = mStompClient.topic(destination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tm -> newGroupMessageReceived(tm.getPayload()),
                        throwable -> clientListener.errorOnSubcribingOnTopic(throwable));


        compositeDisposable.add(dispTopic);
    }

    /**
     * Called when a new message has been sent to the group
     * @param messageInJson the message in json
     */
    private void newGroupMessageReceived(String messageInJson) {
        Message message = jsonHelper.convertJsonToChatMessage(messageInJson);
        clientListener.newGroupMessageReceived(message);

    }

    /**
     * We are creating  a subscription to be able to receive info about the group
     * @param destination the path to subscribe on
     */
    private void createRecevingGroupInfoSubscription(String destination){
        Disposable dispTopic = mStompClient.topic(destination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tm -> newGroupInfoReceived(tm.getPayload()),
                        throwable -> clientListener.errorOnSubcribingOnTopic(throwable));


        compositeDisposable.add(dispTopic);
    }


    /**
     * Called when the client recevies new info about the group
     * @param groupInfoInJson
     */
    private void newGroupInfoReceived(String groupInfoInJson) {
        Group group = jsonHelper.convertJsonToGroup(groupInfoInJson);
        clientListener.onOldMessagesReceived(group.getMessages());
        clientListener.updateMembersList(group.getUsers());
        if(firstTime){
            clientListener.onClientReady();
            firstTime=false;
        }
    }

    /**
     * Sending a message to the chat
     */
    public void sendMessageToChat(Message message){
        if(message!=null){ //&& myGroup!=null){
            compositeDisposable.add(mStompClient.send(CHAT_PREFIX+groupID+CHAT_SEND_MESSAGE_SUFFIX, jsonHelper.convertChatMessageToJSon(message))
                    .compose(applySchedulers()).subscribe(()
                                    -> Log.d(TAG, "Message send successfully"),
                            throwable -> clientListener.errorWhileSendingMessage(throwable)));
        }else{
            Log.d(TAG,"Didn't send message since it was null");
        }

    }

    /**
     * Sends a message to find a group
     * @param message message in json format
     */
    private void sendFindGroupMessage(String message) {
        if(message!=null){
            compositeDisposable.add(mStompClient.send(ASK_FOR_GROUP_NUMBER, message)
                    .compose(applySchedulers()).subscribe(()
                                    -> Log.d(TAG, "Message send successfully"),
                            throwable -> clientListener.errorWhileSendingMessage(throwable)));
        }else{
            Log.d(TAG,"Didn't send message since it was null");
        }
    }

    /**
     * Sends leave message to server on main thread
     * @param destination The path we want to send to
     */
    public void sendLeaveMessage(String destination){
        compositeDisposable.add(mStompClient.send(destination,jsonHelper.convertChatMessageToJSon(new Message(null, CurrentSession.getCurrentUser(),MessageType.LEAVE)))
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()
                                -> Log.d(TAG, "Leave message send successfully"),
                        throwable -> clientListener.errorWhileSendingMessage(throwable)));

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

    /**
     * Creates a json string which represents a find group message.
     * @return a json string
     */
    private String createFindGroupMessage() {
        return jsonHelper.createFindGroupMessage();
    }

    /**
     * Disconnects from server and disposes the subscriptions
     */
    public void disconnect() {
        mStompClient.disconnect();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    /**
     * Sends a message to the server and asks for info about a group
     */
    public void askForGroupInfo() {
        compositeDisposable.add(mStompClient.send(CHAT_PREFIX+groupID+CHAT_ASK_FOR_GROUP_INFO,jsonHelper.convertChatMessageToJSon(new Message(null,CurrentSession.getCurrentUser(),MessageType.CHAT)))
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()
                                -> Log.d(TAG, "Leave message send successfully"),
                        throwable -> clientListener.errorWhileSendingMessage(throwable)));
    }

    private void askForUserInfo(User user){
        RestClient.getInstance().getUserRepository().getUser(user.getUserId())
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myData -> {clientListener.userInfoReceived(myData);}, throwable -> {
        Log.d(TAG, throwable.toString());

    });
    }

    /**
     * Sends a leave message to the server, disconnects and disposes any subscriptions
     */
    public void leaveGroup() {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        if(mStompClient.isConnected()) {
            sendLeaveMessage(CHAT_PREFIX + groupID + CHAT_LEAVING_GROUP_SUFFIX);
            mStompClient.disconnect();
        }
    }

    public String getGroupId(){
        return groupID;
    }
}
