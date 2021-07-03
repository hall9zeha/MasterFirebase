package com.barryzea.firechat.ChatModule.model.dataAccess;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.firechat.ChatModule.model.LastConnectionEventListener;
import com.barryzea.firechat.ChatModule.model.MessagesEventListener;
import com.barryzea.firechat.ChatModule.model.SendMessageListener;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.model.dataAccess.FirebaseRealtimeDatabaseApi;
import com.barryzea.firechat.common.pojo.Message;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabaseChat {
    private static final String  PATH_CHATS="chats";
    private static final String PATH_MESSAGES="messages";

    private FirebaseRealtimeDatabaseApi mDatabaseApi;
    private ChildEventListener mMessagesEventListener;
    private ValueEventListener mFriendProfileListener;


    public RealtimeDatabaseChat() {
        mDatabaseApi = FirebaseRealtimeDatabaseApi.getInstance();
    }

    public FirebaseRealtimeDatabaseApi getmDatabaseApi() {
        return mDatabaseApi;
    }

    public void subscribeToMessages(String myEmail, String friendEmail, MessagesEventListener listener){
        if(mMessagesEventListener == null){
            mMessagesEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                    listener.onMessageReceived(getMessage(snapshot));
                }

                @Override
                public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull  DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {
                    switch(error.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.chat_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };
        }
        getChatMessageReference(myEmail, friendEmail).addChildEventListener(mMessagesEventListener);
    }

    private DatabaseReference getChatMessageReference(String myEmail, String friendEmail) {
        return getChatsReference(myEmail, friendEmail).child(PATH_MESSAGES);
    }

    private DatabaseReference getChatsReference(String myEmail, String friendEmail) {
        String myEmailEncoded= UtilsCommon.getEmailEncoded(myEmail);
        String friendEmailEncoded= UtilsCommon.getEmailEncoded(friendEmail);

        String keyChat=myEmailEncoded + FirebaseRealtimeDatabaseApi.SEPARATOR + friendEmailEncoded;
        if(myEmailEncoded.compareTo(friendEmailEncoded)>0){
            keyChat=friendEmailEncoded + FirebaseRealtimeDatabaseApi.SEPARATOR + myEmailEncoded;

        }

        return mDatabaseApi.getRootReference().child(PATH_CHATS).child(keyChat);
    }

    private Message getMessage(DataSnapshot snapshot) {
        Message message= snapshot.getValue(Message.class);
        if(message !=null){
            message.setUid(snapshot.getKey());
        }
        return message;
    }
    public void unsubscribeToMessages(String myEmail,  String friendEmail){
        if(mMessagesEventListener != null){
            getChatMessageReference(myEmail, friendEmail).removeEventListener(mMessagesEventListener);
        }
    }
    public void subscribeToFriend(String uid, LastConnectionEventListener listener){
        if(mFriendProfileListener ==null){
            mFriendProfileListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    long lastConnectionFriend=0;
                    String uidConnectionFriend="";
                    try{
                        Long value =snapshot.getValue(Long.class);
                        if(value != null){
                            lastConnectionFriend= value;
                        }

                    }
                    catch(Exception e){
                        e.printStackTrace();
                        String lastConnectionWith=snapshot.getValue(String.class);
                        if(lastConnectionWith!=null && !lastConnectionWith.isEmpty()){
                            String[]values=lastConnectionWith.split(FirebaseRealtimeDatabaseApi.SEPARATOR);
                            if(values.length>0){
                                lastConnectionFriend=Long.valueOf(values[0]);
                                if(values.length>1){
                                    uidConnectionFriend= values[1];
                                }
                            }
                        }
                    }
                    listener.onSuccess(lastConnectionFriend == Constants.ONLINE_VALUE, lastConnectionFriend,
                            uidConnectionFriend);

                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            };
        }
        mDatabaseApi.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                .addValueEventListener(mFriendProfileListener);
    }
    public void unsubscribeToFriend(String uid){
        if(mFriendProfileListener != null){
            mDatabaseApi.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
            mDatabaseApi.getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH)
                    .removeEventListener(mFriendProfileListener);
        }
    }
    public void setMessagesRead(String myUid, String friendUid){
        final DatabaseReference userReference = getOneContactsReference(myUid, friendUid);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if(user != null){
                    Map<String, Object> updates= new HashMap<>();
                    updates.put(User.MESSAGES_UNREAD, 0);
                    userReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.v("Unread", error.getMessage());
            }
        });
    }

    private DatabaseReference getOneContactsReference(String uidMain, String uidChild) {
            return mDatabaseApi.getUserReferenceByUid(uidMain).child(FirebaseRealtimeDatabaseApi.PATH_CONTACTS)
                    .child(uidChild);
    }
    public  void sumUnreadMessages(String myUid, String friendUid){
        final DatabaseReference userReference = getOneContactsReference(friendUid, myUid);
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull

            @Override
            public Transaction.Result doTransaction(@NonNull  MutableData currentData) {
                User user= currentData.getValue(User.class);
                if(user==null){
                    return Transaction.success(currentData);
                }
                user.setMessagesUnread(user.getMessagesUnread() + 1 );
                currentData.setValue(user);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable  DatabaseError error, boolean committed, @Nullable  DataSnapshot currentData) {

            }
        });
    }
    public void sendMessage(String msg, String photoUrl, String friendEmail, User myUser,
                            SendMessageListener listener ){
            Message message= new Message();
            message.setSender(myUser.getEmail());
            message.setMsg(msg);
            message.setPhotoUrl(photoUrl);

            DatabaseReference chatReference = getChatMessageReference(myUser.getEmail(), friendEmail);
            chatReference.push().setValue(message, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull  DatabaseReference ref) {
                    if(error == null){
                        listener.onSuccess();
                    }
                }
            });
    }
}
