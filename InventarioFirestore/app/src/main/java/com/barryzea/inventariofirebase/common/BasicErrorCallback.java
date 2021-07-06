package com.barryzea.inventariofirebase.common;

public interface BasicErrorCallback {
    void onSuccess();
    void onError(int typeEvent, int resMsg);
}
