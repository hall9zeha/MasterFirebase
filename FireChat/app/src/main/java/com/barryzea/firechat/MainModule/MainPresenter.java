package com.barryzea.firechat.MainModule;

import com.barryzea.firechat.MainModule.events.MainEvent;
import com.barryzea.firechat.common.pojo.User;

public interface MainPresenter {
    void onCreate();
    void onResume();
    void onPause();
    void onDestroy();

    void signOff();
    User getCurrentUser();
    void removeFriends(String friendUid);
    void acceptRequest(User user);
    void denyRequest(User user);
    void onEventListener(MainEvent event);
}
