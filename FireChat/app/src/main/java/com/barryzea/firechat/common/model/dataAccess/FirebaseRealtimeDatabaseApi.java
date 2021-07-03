package com.barryzea.firechat.common.model.dataAccess;

import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.pojo.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRealtimeDatabaseApi {
    public static final String SEPARATOR="___&___";
    public static final String PATH_USERS="users";
    public static final String PATH_CONTACTS="contacts";
    public static final String PATH_REQUEST="request";

    private DatabaseReference mDatabaseReference;

    public DatabaseReference getContactReference(String uid) {
        return getUserReferenceByUid(uid).child(PATH_CONTACTS);
    }

    public DatabaseReference getRequestReference(String email) {
        return getRootReference().child(PATH_REQUEST).child(email);
    }

    private static class SingletonHolder{

        private static final FirebaseRealtimeDatabaseApi INSTANCE= new FirebaseRealtimeDatabaseApi();

    }
    public static FirebaseRealtimeDatabaseApi getInstance(){
        return SingletonHolder.INSTANCE;
    }
    private FirebaseRealtimeDatabaseApi(){
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public DatabaseReference getRootReference(){
        return mDatabaseReference.getRoot();
    }
    public DatabaseReference getUserReferenceByUid(String uid){
        return getRootReference().child(PATH_USERS).child(uid);
    }
    public void updateMyLastConnection(boolean online, String uid) {
        updateMyLastConnection(online,"", uid);
    }
    public void updateMyLastConnection(boolean online, String uidFriend, String uid ){
        String lastConnectionWith= Constants.ONLINE_VALUE + SEPARATOR + uidFriend;
        Map<String, Object> values = new HashMap<>();
        values.put(User.LAST_CONNECTION_WITH, online?lastConnectionWith: ServerValue.TIMESTAMP);
        //offline
        getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).keepSynced(true);
        getUserReferenceByUid(uid).updateChildren(values);

        if(online){
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect()
                    .setValue(ServerValue.TIMESTAMP);

        }
        else{
            getUserReferenceByUid(uid).child(User.LAST_CONNECTION_WITH).onDisconnect().cancel();
        }
    }

}
