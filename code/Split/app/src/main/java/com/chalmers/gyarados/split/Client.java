package com.chalmers.gyarados.split;

import android.util.Log;

import com.chalmers.gyarados.split.model.Group;
import com.chalmers.gyarados.split.model.Message;

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
     * The ip we want to connect to, given by activity before
     */
    private String ip;

    /**
     * Used for logging
     */
    private static final String TAG = "ClientActivity";

    /**
     * The group the server gives to me
     */

    private Group myGroup;

    /**
     * An object that helps us creating messages in json format
     */
    private JSONHelper jsonHelper;

    /**
     * An object that wants to receive information about what the client receives
     */
    private ClientListener clientListener;

    public Client(String ip, ClientListener clientListener) {
        this.ip = ip;
        this.clientListener = clientListener;
        compositeDisposable = new CompositeDisposable();
        jsonHelper=new JSONHelper();
    }

    /**
     * Connects to the server.
     * Subscribes on a topic and sends a join message.
     */
    public void connectStomp(){

        //Which ip-adress we want to connect to
        String uri= "ws://"+ip+"/split/websocket";
        Log.d(TAG,uri);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, uri );

        //We want to subscribe on a topic to be able to receive our group number
        Disposable dispTopic = mStompClient.topic(RECEIVE_GROUP_NUMBER)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage ->{
                    //Now we have received a group
                    myGroup = jsonHelper.convertJsonToGroup(topicMessage.getPayload());
                    clientListener.updateMembersList(myGroup.getUsers());


                    //We want to subscribe to messages on our given group
                    createReceivingMessageSubscription("/topic/"+myGroup.getId());
                    //We want to subscribe on group info
                    createRecevingGroupInfoSubscription("/user/queue/getInfo/"+myGroup.getId());

                    clientListener.onOldMessagesReceived(myGroup.getMessages());
                    clientListener.onClientReady();


                }, throwable -> {
                    clientListener.errorOnSubcribingOnTopic(throwable);

                });

        compositeDisposable.add(dispTopic);

        //We want to ask the server for a group number.
        sendFindGroupMessage(createFindGroupMessage());


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
        clientListener.updateMembersList(group.getUsers());
    }

    /**
     * Sending a message to the chat
     */
    public void sendMessageToChat(Message message){
        if(message!=null && myGroup!=null){
            compositeDisposable.add(mStompClient.send(CHAT_PREFIX+myGroup.getId()+CHAT_SEND_MESSAGE_SUFFIX, jsonHelper.convertChatMessageToJSon(message))
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
        compositeDisposable.add(mStompClient.send(destination,jsonHelper.createChatMessage(CurrentSession.getCurrentUser(),null,"LEAVE"))
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
        return jsonHelper.createFindGroupMessage(CurrentSession.getCurrentUser(),57.684027,11.975490,57.735473, 12.112732);
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
        compositeDisposable.add(mStompClient.send(CHAT_PREFIX+myGroup.getId()+CHAT_ASK_FOR_GROUP_INFO,jsonHelper.createChatMessage(CurrentSession.getCurrentUser(),null,"CHAT"))
                .unsubscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()
                                -> Log.d(TAG, "Leave message send successfully"),
                        throwable -> clientListener.errorWhileSendingMessage(throwable)));
    }

    /**
     * Sends a leave message to the server, disconnects and disposes any subscriptions
     */
    public void leaveGroup() {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        if(mStompClient.isConnected()) {
            sendLeaveMessage(CHAT_PREFIX + myGroup.getId() + CHAT_LEAVING_GROUP_SUFFIX);
            mStompClient.disconnect();
        }
    }
}
