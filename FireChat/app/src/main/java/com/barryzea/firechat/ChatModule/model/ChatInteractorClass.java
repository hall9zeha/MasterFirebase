package com.barryzea.firechat.ChatModule.model;

import android.app.Activity;
import android.net.Uri;

import com.barryzea.firechat.ChatModule.events.ChatEvent;
import com.barryzea.firechat.ChatModule.model.dataAccess.NotificationRS;
import com.barryzea.firechat.ChatModule.model.dataAccess.RealtimeDatabaseChat;
import com.barryzea.firechat.ChatModule.model.dataAccess.Storage;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseAuthenticationApi;
import com.barryzea.firechat.common.pojo.Message;
import com.barryzea.firechat.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class ChatInteractorClass implements ChatInteractor {
    private RealtimeDatabaseChat mDatabase;
    private FirebaseAuthenticationApi mAuthenticationApi;
    private Storage mStorage;

    //notify
    private NotificationRS mNotificationRs;
    private User myUser;
    private String myFriendUid;
    private String mFriendEmail;

    private long mLastConnectionFriend;
    private String mUidConnectedFriend ="";

    public ChatInteractorClass() {
        this.mDatabase= new RealtimeDatabaseChat();
        this.mAuthenticationApi= FirebaseAuthenticationApi.getInstance();
        this.mStorage= new Storage();
        this.mNotificationRs= new NotificationRS();
    }

    private User getCurrentUser(){
        if(myUser==null){
            myUser=mAuthenticationApi.getAuthUser();
        }
        return myUser;
    }

    @Override
    public void subscribeToFriend(String friendUid, String friendEmail) {
        this.mFriendEmail=friendEmail;
        this.myFriendUid=friendUid;

        mDatabase.subscribeToFriend(friendUid, new LastConnectionEventListener() {
            @Override
            public void onSuccess(boolean online, long lastConnection, String uidConnectedFriend) {
                postStatusFriend(online, lastConnection);
                mUidConnectedFriend=uidConnectedFriend;
                mLastConnectionFriend=lastConnection;
            }
        });
        mDatabase.setMessagesRead(getCurrentUser().getUid(), friendUid);
    }


    @Override
    public void unsubscribeToFriend(String friendUid) {
        mDatabase.unsubscribeToFriend(friendUid);

    }

    @Override
    public void subscribeToMessage() {
        mDatabase.subscribeToMessages(getCurrentUser().getEmail(), mFriendEmail,
                new MessagesEventListener() {
                    @Override
                    public void onMessageReceived(Message message) {
                        String msgSender = message.getSender();
                        message.setSentByMe(msgSender.equals(getCurrentUser().getEmail()));
                        postMessage(message);
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ChatEvent.ERROR_SERVER, resMsg);
                    }
                });
        mDatabase.getmDatabaseApi().updateMyLastConnection(Constants.ONLINE, myFriendUid, getCurrentUser().getUid());
    }

    @Override
    public void unsubscribeToMessage() {
        mDatabase.unsubscribeToMessages(getCurrentUser().getEmail(), mFriendEmail);
        mDatabase.getmDatabaseApi().updateMyLastConnection(Constants.OFFLINE, getCurrentUser().getUid());
    }

    @Override
    public void sendMessage(String msg) {
        sendMessage(msg, null);
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        mStorage.uploadImageChat(activity, imageUri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri newUri) {
                sendMessage(null, newUri.toString());
                postUploadSuccess();

            }

            @Override
            public void onError(int resMsg) {
                post(ChatEvent.IMAGE_UPLOAD_FAIL, resMsg);
            }
        });
    }

    private void sendMessage(final String msg, String photoUrl){
        mDatabase.sendMessage(msg, photoUrl, mFriendEmail, getCurrentUser(),
                new SendMessageListener() {
                    @Override
                    public void onSuccess() {
                        if(!mUidConnectedFriend.equals(getCurrentUser().getUid())){
                            mDatabase.sumUnreadMessages(getCurrentUser().getUid(), myFriendUid);

                            if(mLastConnectionFriend != Constants.ONLINE_VALUE){
                                mNotificationRs.setSendNotification(getCurrentUser().getUserName(), msg
                                        , mFriendEmail, getCurrentUser().getUid(), getCurrentUser().getEmail(),
                                        getCurrentUser().getUri(), new EventErrorTypeListener() {
                                            @Override
                                            public void onError(int typeEvent, int resMsg) {
                                                post(typeEvent, resMsg);
                                            }
                                        });
                            }
                        }
                    }
                });
    }
    private void postUploadSuccess(){
        post(ChatEvent.IMAGE_UPLOAD_SUCCESS, 0 ,null, false,0);
    }
    private void postMessage(Message message){
        post(ChatEvent.MESSAGE_ADDED, 0, message, false, 0);
    }
    private void post(int typeEvent, int resMsg){
        post(typeEvent, resMsg, null, false, 0);
    }

    private void postStatusFriend(boolean online, long lastConnection) {
        post(ChatEvent.GET_STATUS_FRIEND, 0, null, online, lastConnection);
    }
    private void post(int typeEvent, int resMsg, Message message, boolean online, long lastConnection){
        ChatEvent event = new ChatEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        event.setMessage(message);
        event.setConnected(online);
        event.setLastConnection(lastConnection);
        EventBus.getDefault().post(event);
    }

}
