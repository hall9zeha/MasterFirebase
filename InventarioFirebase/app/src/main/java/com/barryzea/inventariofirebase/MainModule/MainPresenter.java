package com.barryzea.inventariofirebase.MainModule;

import com.barryzea.inventariofirebase.MainModule.events.MainEvent;
import com.barryzea.inventariofirebase.common.pojo.Product;

public interface MainPresenter {
    void onCreate();
    void onPause();
    void onResume();
    void onDestroy();

    void remove(Product product);
    void onEventListener(MainEvent event);
}
