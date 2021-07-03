package com.barryzea.firechat.ChatModule.view;

import android.content.Intent;

import com.barryzea.firechat.common.pojo.Message;

public interface ChatView {
    void showProgress();
    void hideProgress();

    void onStatusUser(boolean connected, long lastConnection );
    void onError(int resMsg);

    void onMessageReceived(Message msg);
    void openDialogPreview(Intent data);
}
