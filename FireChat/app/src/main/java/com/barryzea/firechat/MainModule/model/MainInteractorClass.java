package com.barryzea.firechat.MainModule.model;

import com.barryzea.firechat.MainModule.events.MainEvent;
import com.barryzea.firechat.MainModule.model.dataAccess.AuthenticationMain;
import com.barryzea.firechat.MainModule.model.dataAccess.RealtimeDatabaseMain;
import com.barryzea.firechat.MainModule.model.dataAccess.UserEventListener;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.model.BasicEventsCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseCloudMessagesApi;
import com.barryzea.firechat.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class MainInteractorClass implements MainInteractor {
    private RealtimeDatabaseMain mDatabase;
    private AuthenticationMain mAuthetication;
    //notificaciones
    private FirebaseCloudMessagesApi mCloudMessagesApi;

    private User myUser;

    public MainInteractorClass() {
        mDatabase=new RealtimeDatabaseMain();
        mAuthetication=new AuthenticationMain();
        //notificaciones
        mCloudMessagesApi = FirebaseCloudMessagesApi.getInstance();
    }

    @Override
    public void subscribeToUserList() {
        mDatabase.subscribeToUserList(getCurrentUser().getUid(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                post(MainEvent.USER_ADDED, user);
            }

            @Override
            public void onUserUpdated(User user) {
                post(MainEvent.USER_UPDATED, user);
            }

            @Override
            public void onUserRemoved(User user) {
                post(MainEvent.USER_REMOVED, user);
            }

            @Override
            public void onError(int resMsg) {
                postError(MainEvent.ERROR_SERVER, resMsg);
            }
        });
        mDatabase.subscribeToRequest(getCurrentUser().getEmail(), new UserEventListener() {
            @Override
            public void onUserAdded(User user) {
                post(MainEvent.REQUEST_ADDED, user);
            }

            @Override
            public void onUserUpdated(User user) {
                post(MainEvent.USER_UPDATED, user);
            }

            @Override
            public void onUserRemoved(User user) {
                post(MainEvent.REQUEST_REMOVED, user);
            }

            @Override
            public void onError(int resMsg) {
                post(MainEvent.ERROR_SERVER);
            }
        });
        changeConnectionStatus(Constants.ONLINE);


    }

    private void changeConnectionStatus(boolean online) {
        mDatabase.getmDataBaseApi().updateMyLastConnection(online, getCurrentUser().getUid());
    }

    private void post(int typeError){
        post(typeError, null, 0);
    }


    @Override
    public void unsubscribeToUserList() {
        mDatabase.unSubscribeToUsers(getCurrentUser().getUid());
        mDatabase.unSubscribeToRequest(getCurrentUser().getEmail());
        changeConnectionStatus(Constants.OFFLINE);
    }

    @Override
    public void signOff() {
        //notificaciones
        mCloudMessagesApi.unSubscribeToMyTopic(getCurrentUser().getEmail());
        mAuthetication.signOff();
    }

    @Override
    public User getCurrentUser() {
        return myUser ==null ? mAuthetication.getmAuthenticationApi().getAuthUser() : myUser;
    }

    @Override
    public void removeFriend(String friendUid) {
        mDatabase.removeUser(friendUid, getCurrentUser().getUid(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.USER_REMOVED);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void acceptRequest(User user) {
        mDatabase.acceptRequest(user, getCurrentUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_ACCEPTED, user);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    @Override
    public void denyRequest(User user) {
        mDatabase.denyRequest(user, getCurrentUser().getEmail(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.REQUEST_DENIED);
            }

            @Override
            public void onError() {
                post(MainEvent.ERROR_SERVER);
            }
        });
    }

    private void postError(int typeError, int resMsg) {
        post(typeError, null, resMsg);
    }
    private void post(int typeEvent, User user){
        post(typeEvent, user,0);
    }

    private void post(int typeEvent, User user, int resMsg) {
        MainEvent event= new MainEvent();
        event.setTypeEvent(typeEvent);
        event.setUser(user);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }
}
