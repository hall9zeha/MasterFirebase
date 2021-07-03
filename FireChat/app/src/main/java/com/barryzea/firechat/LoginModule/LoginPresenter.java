package com.barryzea.firechat.LoginModule;

import android.content.Intent;

import com.barryzea.firechat.LoginModule.events.LoginEvent;

public interface LoginPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    void result(int requestCode, int resultCode, Intent data);
    void getStatusAuth();
    void eventListener(LoginEvent event);
}
