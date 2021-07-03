package com.barryzea.firechat.LoginModule.model.dataAccess;

import androidx.annotation.NonNull;

import com.barryzea.firechat.LoginModule.events.LoginEvent;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.model.dataAccess.FirebaseRealtimeDatabaseApi;
import com.barryzea.firechat.common.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabaseLogin {
    private FirebaseRealtimeDatabaseApi mDataBaseApi;

    public RealtimeDatabaseLogin() {
        mDataBaseApi= FirebaseRealtimeDatabaseApi.getInstance();
    }
    public void registerUser(User user){
        Map<String, Object> values=new HashMap<>();
        values.put(User.USERNAME, user.getUserName());
        values.put(User.EMAIL, user.getEmail());
        values.put(User.PHOTO_URL,  user.getPhotoUrl());

        mDataBaseApi.getUserReferenceByUid(user.getUid()).updateChildren(values);
    }
    public void checkUserExist(String uid, EventErrorTypeListener listener){
        mDataBaseApi.getUserReferenceByUid(uid).child(User.EMAIL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            listener.onError(LoginEvent.USER_NOT_EXIST, R.string.login_error_user_exist);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                        listener.onError(LoginEvent.ERROR_SERVER, R.string.login_message_error);
                    }
                });
    }
}
