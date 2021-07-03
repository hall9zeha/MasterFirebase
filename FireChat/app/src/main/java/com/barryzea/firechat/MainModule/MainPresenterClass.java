package com.barryzea.firechat.MainModule;

import com.barryzea.firechat.MainModule.events.MainEvent;
import com.barryzea.firechat.MainModule.model.MainInteractor;
import com.barryzea.firechat.MainModule.model.MainInteractorClass;
import com.barryzea.firechat.MainModule.view.MainView;
import com.barryzea.firechat.common.pojo.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainPresenterClass  implements  MainPresenter{
    private MainView mainView;
    private MainInteractor mInteractor;

    public MainPresenterClass(MainView mainView) {
        this.mainView = mainView;
        this.mInteractor= new MainInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        mainView=null;
    }
    @Override
    public void onResume() {
        if(mainView != null){
            mInteractor.subscribeToUserList();
        }
    }

    @Override
    public void onPause() {
        if(mainView != null){
            mInteractor.unsubscribeToUserList();
        }
    }

    @Override
    public void signOff() {
        mInteractor.unsubscribeToUserList();
        mInteractor.signOff();
        onDestroy();
    }

    @Override
    public User getCurrentUser() {
        return mInteractor.getCurrentUser();
    }

    @Override
    public void removeFriends(String friendUid) {
        if(mainView != null){
            mInteractor.removeFriend(friendUid);
        }
    }

    @Override
    public void acceptRequest(User user) {
        if(mainView != null){
            mInteractor.acceptRequest(user);
        }
    }

    @Override
    public void denyRequest(User user) {
        if(mainView != null){
            mInteractor.denyRequest(user);
        }
    }
    @Subscribe
    @Override
    public void onEventListener(MainEvent event) {
        if(mainView != null){
            User user = event.getUser();
            switch(event.getTypeEvent()){
                case MainEvent.USER_ADDED:
                    mainView.friendAdded(user);
                    break;
                case MainEvent.USER_UPDATED:
                    mainView.friendUpdated(user);
                    break;
                case MainEvent.USER_REMOVED:
                    if (user != null) {
                        mainView.friendRemoved(user);
                    }
                    else{
                        mainView.showFriendRemoved();
                    }
                    break;
                case MainEvent.REQUEST_ADDED:
                    mainView.requestAdded(user);
                    break;
                case MainEvent.REQUEST_UPDATED:
                    mainView.requestUpdated(user);
                    break;
                case MainEvent.REQUEST_REMOVED:
                    mainView.requestRemoved(user);
                    break;
                case MainEvent.REQUEST_ACCEPTED:
                    mainView.showRequestAccepted(user.getUserName());
                    break;
                case MainEvent.REQUEST_DENIED:
                    mainView.showRequestDenied();
                    break;
                case MainEvent.ERROR_SERVER:
                    mainView.showError(event.getResMsg());
                    break;
            }
        }
    }
}
