package com.barryzea.firechat.ChatModule.model;

import com.barryzea.firechat.common.pojo.Message;

public interface MessagesEventListener {
    void onMessageReceived(Message message);
    void onError(int resMsg);

}
