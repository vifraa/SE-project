package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.Message;
import com.chalmers.gyarados.split.model.User;

import java.util.List;

interface ClientListener {
    void updateMembersList(List<User> users);

    void newGroupMessageReceived(Message message);

    void errorOnSubcribingOnTopic(Throwable throwable);

    void errorOnLifeCycle();

    void errorWhileSendingMessage(Throwable throwable);

    void onOldMessagesReceived(List<Message> messages);

    void onClientReady();

    void userInfoReceived(User myData);

    void errorOnLifeCycleFirstConnect();

    void onConnectionClosedFirstConnect();

    void onConnectionClosed();

    void onConnectionOpened();

    void onReConnectingFailed();

    void onReconnectingSuccess();

    void onMessagesReceivedWhenDisconnected(List<Message> listWithMessages);
}
