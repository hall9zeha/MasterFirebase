package com.barryzea.firechat.AddModule.model.dataAccess;

import com.barryzea.firechat.common.model.BasicEventsCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseRealtimeDatabaseApi;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabaseAdd {
    private FirebaseRealtimeDatabaseApi mDatabaseApi;

    public RealtimeDatabaseAdd() {
        mDatabaseApi= FirebaseRealtimeDatabaseApi.getInstance();
    }

    public void addFriends(String email, User myUser, final BasicEventsCallback callback){
        Map<String, Object> myUserMap= new HashMap<>();
        myUserMap.put(User.USERNAME, myUser.getUserName());
        myUserMap.put(User.EMAIL, myUser.getEmail());
        myUserMap.put(User.PHOTO_URL, myUser.getPhotoUrl());

        final String emailEncode= UtilsCommon.getEmailEncoded(email);
        DatabaseReference userReference= mDatabaseApi.getRequestReference(emailEncode);
        userReference.child(myUser.getUid()).updateChildren(myUserMap)
                .addOnSuccessListener(unused -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError();
                });
    }
}
