package com.barryzea.firechat.LoginModule;

import android.app.Activity;
import android.content.Intent;

import com.barryzea.firechat.LoginModule.events.LoginEvent;
import com.barryzea.firechat.LoginModule.model.LoginInteractor;
import com.barryzea.firechat.LoginModule.model.LoginInteractorClass;
import com.barryzea.firechat.LoginModule.view.LoginActivity;
import com.barryzea.firechat.LoginModule.view.LoginView;
import com.barryzea.firechat.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginPresenterClass implements LoginPresenter {
    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenterClass(LoginView mView) {
        this.mView = mView;
        mInteractor = new LoginInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        if(setProgress()){
            mInteractor.onResume();
        }
    }


    @Override
    public void onPause() {
        if(setProgress()){
            mInteractor.onPause();
        }
    }

    @Override
    public void onDestroy() {
        mView=null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            switch (resultCode){
                case LoginActivity.RC_SIGN_IN:
                    if(data != null){
                        mView.showLoginSuccessFull(data);
                    }
            }
        }
        else{
            mView.showError(R.string.login_message_error);
        }
    }

    @Override
    public void getStatusAuth() {
        if(setProgress()){
            mInteractor.getStatusAuth();
        }
    }
    @Subscribe
    @Override
    public void eventListener(LoginEvent event) {
        if(mView != null){
            mView.hideProgress();
            switch(event.getTypeEvent()){
                case LoginEvent.STATUS_AUTH_SUCCESS:
                    if(setProgress()){
                        mView.showMessageStarting();
                        mView.openMainActivity();
                    }
                    break;
                case LoginEvent.STATUS_AUTH_ERROR:
                    mView.openUILogin();
                    break;
                case LoginEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;

            }
        }
    }
    private boolean setProgress() {
        if(mView != null){
            mView.showProgress();
            return true;
        }
        return false;
    }
}
