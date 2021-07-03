package com.barryzea.firechat.ChatModule.model;

public interface LastConnectionEventListener {
    void onSuccess(boolean online, long lastConnection, String uidConnectedFriend);

}
