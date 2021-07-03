package com.barryzea.firechat.common.model.dataAccess;

import com.barryzea.firechat.common.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationApi {

    private FirebaseAuth firebaseAuth;

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    private static class SingletonHolder{

        private static final FirebaseAuthenticationApi INSTANCE=new FirebaseAuthenticationApi();

    }
    public static FirebaseAuthenticationApi getInstance(){
        return SingletonHolder.INSTANCE;
    }
    private FirebaseAuthenticationApi() {
        this.firebaseAuth=FirebaseAuth.getInstance();
    }
    public FirebaseAuth getmFirebaseAuth() {
        return firebaseAuth;
    }

    public User getAuthUser() {
        User user = new User();
        if(firebaseAuth != null && firebaseAuth.getCurrentUser() != null){
            user.setUid(firebaseAuth.getCurrentUser().getUid());
            user.setUserName(firebaseAuth.getCurrentUser().getDisplayName());
            user.setEmail(firebaseAuth.getCurrentUser().getEmail());
            user.setUri(firebaseAuth.getCurrentUser().getPhotoUrl());

        }
        return user;
    }
}
