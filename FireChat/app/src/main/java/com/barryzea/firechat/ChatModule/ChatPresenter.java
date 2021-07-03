package com.barryzea.firechat.ChatModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.barryzea.firechat.ChatModule.events.ChatEvent;

public interface ChatPresenter {
    void onCreate();
    void onDestroy();
    void onPause();
    void onResume();

    void setUpFriend(String uid, String email);
    void sendMessage(String msg);
    void sendImage(Activity activity, Uri imageUri);
    void result(int requestCode, int resultCode, Intent data);
    void onEventListener(ChatEvent event);

}
