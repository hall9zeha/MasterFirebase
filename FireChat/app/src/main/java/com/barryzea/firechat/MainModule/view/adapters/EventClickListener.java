package com.barryzea.firechat.MainModule.view.adapters;

import com.barryzea.firechat.common.pojo.User;

public interface EventClickListener {
    void onItemClick(User user);
    void onItemLongClick(User user);
    void onAcceptRequest(User user);
    void onDeniedRequest(User user);
}
