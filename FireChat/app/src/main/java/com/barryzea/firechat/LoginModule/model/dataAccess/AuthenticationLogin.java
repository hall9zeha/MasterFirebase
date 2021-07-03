package com.barryzea.firechat.LoginModule.model.dataAccess;

import androidx.annotation.NonNull;

import com.barryzea.firechat.common.model.dataAccess.FirebaseAuthenticationApi;
import com.barryzea.firechat.common.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationLogin {
    private FirebaseAuthenticationApi mAuthentication;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public AuthenticationLogin(){
        mAuthentication=FirebaseAuthenticationApi.getInstance();

    }
    public void onResume(){
        mAuthentication.getmFirebaseAuth().addAuthStateListener(mAuthStateListener);
    }
    public void onPause(){
        if(mAuthStateListener != null) {
            mAuthentication.getmFirebaseAuth().removeAuthStateListener(mAuthStateListener);
        }
    }
    public void getStatusAuth(StatusAuthCallback callback){
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if(user !=null){
                    callback.onGetUser(user);
                }
                else{
                    callback.onLaunchUILogin();
                }
            }
        };
    }
    public User getCurrentUser(){
        return mAuthentication.getAuthUser();
    }

}
