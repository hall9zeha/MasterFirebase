package com.barryzea.firechat.MainModule.model.dataAccess;

import com.barryzea.firechat.common.pojo.User;

public interface UserEventListener {
    void onUserAdded(User user);
    void onUserUpdated(User user);
    void onUserRemoved(User user);

    void onError(int resMsg);
}
