package com.barryzea.firechat.AddModule;

import com.barryzea.firechat.AddModule.events.AddEvent;

public interface AddPresenter {
    void onShow();
    void onDestroy();

    void addFriend(String email);
    void onEventListener(AddEvent event);
}
