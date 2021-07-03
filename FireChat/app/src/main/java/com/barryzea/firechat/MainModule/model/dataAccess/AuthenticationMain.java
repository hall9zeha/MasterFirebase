package com.barryzea.firechat.MainModule.model.dataAccess;

import com.barryzea.firechat.common.model.dataAccess.FirebaseAuthenticationApi;

public class AuthenticationMain {
    private FirebaseAuthenticationApi mAuthenticationApi;

    public AuthenticationMain() {
        mAuthenticationApi = FirebaseAuthenticationApi.getInstance();
    }

    public FirebaseAuthenticationApi getmAuthenticationApi() {
        return mAuthenticationApi;
    }
    public void signOff(){
        mAuthenticationApi.getmFirebaseAuth().signOut();
    }
}
