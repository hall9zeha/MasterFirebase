package com.barryzea.firechat.ProfileModule.model.dataAccess;

public interface UpdateUserListener {
    void onSuccess();
    void onNotifyContacts();
    void onError(int resMsg);

}
