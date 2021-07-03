package com.barryzea.firechat.MainModule.model;

import com.barryzea.firechat.common.pojo.User;

public interface MainInteractor {
    void subscribeToUserList();
    void unsubscribeToUserList();

    void signOff();
    User getCurrentUser();
    void removeFriend(String friendUid);
    void acceptRequest(User user);
    void denyRequest(User user);


}
