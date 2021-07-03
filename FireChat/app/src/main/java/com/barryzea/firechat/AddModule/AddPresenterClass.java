package com.barryzea.firechat.AddModule;

import com.barryzea.firechat.AddModule.events.AddEvent;
import com.barryzea.firechat.AddModule.model.AddInteractor;
import com.barryzea.firechat.AddModule.model.AddInteractorClass;
import com.barryzea.firechat.AddModule.view.AddView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AddPresenterClass implements AddPresenter {
    private AddView mView;
    private AddInteractor mInteractor;

    public AddPresenterClass(AddView mView) {
        this.mView = mView;
        this.mInteractor= new AddInteractorClass();
    }

    @Override
    public void onShow() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void addFriend(String email) {
        if(mView != null){
            mView.disableUIElements();
            mView.showProgress();
            mInteractor.addFriend(email);
        }

    }
    @Subscribe
    @Override
    public void onEventListener(AddEvent event) {
        if(mView != null){
            mView.enableUIElements();
            mView.hideProgress();
            switch(event.getTypeEvent()){
                case AddEvent.SEND_REQUEST_SUCCESS:
                    mView.friendAdded();
                    break;
                case AddEvent.ERROR_SERVER:
                    mView.friendNotAdded();
                    break;
            }
        }
    }
}
