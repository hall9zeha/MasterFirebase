package com.barryzea.firechat.ProfileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.barryzea.firechat.ProfileModule.events.ProfileEvent;

public interface ProfilePresenter {
    void onCreate();
    void onDestroy();

    void setUpUser(String username, String email, String photoUrl);
    void checkMode();

    void updateUsername(String username);
    void updateImage(Uri uri);
    void result(int requestCode, int resultCode, Intent data);

    void onEventListener(ProfileEvent event);
}
