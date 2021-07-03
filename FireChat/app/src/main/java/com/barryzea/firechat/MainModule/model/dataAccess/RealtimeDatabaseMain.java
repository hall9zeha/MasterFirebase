package com.barryzea.firechat.MainModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.BasicEventsCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseRealtimeDatabaseApi;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabaseMain {

    private FirebaseRealtimeDatabaseApi mDataBaseApi;
    private ChildEventListener mUserEventListener;
    private ChildEventListener mRequestEventListener;


    public RealtimeDatabaseMain() {
        mDataBaseApi = FirebaseRealtimeDatabaseApi.getInstance();
    }

    public FirebaseRealtimeDatabaseApi getmDataBaseApi() {
        return mDataBaseApi;
    }
    private DatabaseReference getUserReference(){
        return mDataBaseApi.getRootReference().child(FirebaseRealtimeDatabaseApi.PATH_USERS);
    }

    public void subscribeToUserList(String myUid, final UserEventListener listener){
        if(mUserEventListener == null){
            mUserEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable  String previousChildName) {
                    listener.onUserAdded(getUser(snapshot));
                }

                @Override
                public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable String previousChildName) {
                    listener.onUserUpdated(getUser(snapshot));
                }

                @Override
                public void onChildRemoved(@NonNull  DataSnapshot snapshot) {
                    listener.onUserRemoved(getUser(snapshot));
                }

                @Override
                public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {
                    switch (error.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.main_error_permission_denied);
                            break;
                        default:
                            listener.onError(R.string.common_error_server);
                            break;
                    }
                }
            };

        }
        mDataBaseApi.getContactReference(myUid).addChildEventListener(mUserEventListener);
    }

    private User getUser(DataSnapshot snapshot) {
        User user= snapshot.getValue(User.class);
        if(user!= null){
            user.setUid(snapshot.getKey());
        }
        return user;
    }

    public void subscribeToRequest(String email, final UserEventListener listener){
        if(mRequestEventListener == null){
            mRequestEventListener= new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                    listener.onUserAdded(getUser(snapshot));

                }

                @Override
                public void onChildChanged(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {
                    listener.onUserUpdated(getUser(snapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    listener.onUserRemoved(getUser(snapshot));
                }

                @Override
                public void onChildMoved(@NonNull  DataSnapshot snapshot, @Nullable  String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {
                    listener.onError(R.string.common_error_server);
                }
            };
        }
        final String emailEncoded= UtilsCommon.getEmailEncoded(email);
        mDataBaseApi.getRequestReference(emailEncoded).addChildEventListener(mRequestEventListener);
    }

    public void unSubscribeToUsers(String uid) {
        if (mUserEventListener != null) {
            mDataBaseApi.getContactReference(uid).removeEventListener(mUserEventListener);
        }
    }
    public void unSubscribeToRequest(String email){
        if(mRequestEventListener != null){
            final String emailEncoded=UtilsCommon.getEmailEncoded(email);
            mDataBaseApi.getRequestReference(emailEncoded).removeEventListener(mRequestEventListener);
        }
    }
    public void removeUser(String friendUid, String myUid, final BasicEventsCallback callback){
        Map<String, Object> removeUserMap= new HashMap<>();
        removeUserMap.put(myUid+"/"+FirebaseRealtimeDatabaseApi.PATH_CONTACTS +"/"+friendUid, null);
        removeUserMap.put(friendUid+"/"+FirebaseRealtimeDatabaseApi.PATH_CONTACTS+"/"+myUid, null);
        getUserReference().updateChildren(removeUserMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable  DatabaseError error, @NonNull  DatabaseReference ref) {
                 if(error ==null){
                     callback.onSuccess();
                 }
                 else{
                     callback.onError();
                 }
            }
        });
    }
    public void acceptRequest(User myFriend, User myUser, final BasicEventsCallback callback){
        Map<String , String> userRequestMap=new HashMap<>();
        userRequestMap.put(User.USERNAME, myFriend.getUserName());
        userRequestMap.put(User.EMAIL,myFriend.getEmail());
        userRequestMap.put(User.PHOTO_URL, myFriend.getPhotoUrl());

        Map<String, String> myUserMap= new HashMap<>();
        myUserMap.put(User.USERNAME, myUser.getUserName());
        myUserMap.put(User.EMAIL,myUser.getEmail());
        myUserMap.put(User.PHOTO_URL, myUser.getPhotoUrl());

        final String encodeEmail=UtilsCommon.getEmailEncoded(myUser.getEmail());
        Map<String , Object> acceptRequest=new HashMap<>();

        acceptRequest.put(FirebaseRealtimeDatabaseApi.PATH_USERS +"/"+myFriend.getUid()+"/"+
                FirebaseRealtimeDatabaseApi.PATH_CONTACTS+"/"+myUser.getUid(), myUserMap);
        acceptRequest.put(FirebaseRealtimeDatabaseApi.PATH_USERS+"/"+myUser.getUid()+"/"+
                FirebaseRealtimeDatabaseApi.PATH_CONTACTS+"/"+myFriend.getUid(), userRequestMap);
        acceptRequest.put(FirebaseRealtimeDatabaseApi.PATH_REQUEST+"/"+encodeEmail+"/"+
                myFriend.getUid(), null);
        mDataBaseApi.getRootReference().updateChildren(acceptRequest, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable  DatabaseError error, @NonNull  DatabaseReference ref) {
               if(error==null){
                   callback.onSuccess();
               }
               else {
                   callback.onError();
               }
            }
        });
    }
    public void denyRequest(User user, String myEmail, final BasicEventsCallback callback){
        final String emailEncoded = UtilsCommon.getEmailEncoded(myEmail);
        mDataBaseApi.getRequestReference(emailEncoded).child(user.getUid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable  DatabaseError error, @NonNull DatabaseReference ref) {
                     if(error==null){
                         callback.onSuccess();
                     }
                     else{
                         callback.onError();
                     }
            }
        });
    }

}
