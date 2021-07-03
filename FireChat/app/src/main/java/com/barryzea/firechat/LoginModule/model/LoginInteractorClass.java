package com.barryzea.firechat.LoginModule.model;

import com.barryzea.firechat.LoginModule.events.LoginEvent;
import com.barryzea.firechat.LoginModule.model.dataAccess.AuthenticationLogin;
import com.barryzea.firechat.LoginModule.model.dataAccess.RealtimeDatabaseLogin;
import com.barryzea.firechat.LoginModule.model.dataAccess.StatusAuthCallback;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.model.dataAccess.FirebaseCloudMessagesApi;
import com.barryzea.firechat.common.pojo.User;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

public class LoginInteractorClass implements LoginInteractor {
    private AuthenticationLogin mAuthentication;
    private RealtimeDatabaseLogin mRealtimeDatabase;
    //notificaciones
    private FirebaseCloudMessagesApi mCloudMessageApi;

    public LoginInteractorClass() {
        mAuthentication= new AuthenticationLogin();
        mRealtimeDatabase = new RealtimeDatabaseLogin();
        //notificaciones
        mCloudMessageApi= FirebaseCloudMessagesApi.getInstance();
    }

    @Override
    public void onResume() {
        mAuthentication.onResume();
    }

    @Override
    public void onPause() {
        mAuthentication.onPause();
    }

    @Override
    public void getStatusAuth() {
       mAuthentication.getStatusAuth(new StatusAuthCallback() {
           @Override
           public void onGetUser(FirebaseUser user) {
                post(LoginEvent.STATUS_AUTH_SUCCESS, user);
                mRealtimeDatabase.checkUserExist(mAuthentication.getCurrentUser().getUid(), new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        if(typeEvent==LoginEvent.USER_NOT_EXIST){
                            registerUser();
                        }
                        else{
                            post(typeEvent);
                        }
                    }
                });
                mCloudMessageApi.subscribeToMyTopic(user.getEmail());
           }

           @Override
           public void onLaunchUILogin() {
                post(LoginEvent.STATUS_AUTH_ERROR);
           }
       });
    }

    private void registerUser() {
        User user = mAuthentication.getCurrentUser();
        mRealtimeDatabase.registerUser(user);
    }

    private void post(int typeEvent) {
        post(typeEvent, null);
    }

    private void post(int typeEvent, FirebaseUser user) {
        LoginEvent event = new LoginEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        EventBus.getDefault().post(event);
    }
}
