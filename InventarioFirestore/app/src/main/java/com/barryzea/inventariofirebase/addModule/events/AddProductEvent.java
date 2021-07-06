package com.barryzea.inventariofirebase.addModule.events;

public class AddProductEvent {
    public static final  int SUCCESS_ADD=0;
    public static final int ERROR_SERVER=101;
    public static final int ERROR_MAX_VALUE=102;
    private int typeEvent;
    private int resMsg;

    public AddProductEvent() {
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }

    public int getResMsg() {
        return resMsg;
    }

    public void setResMsg(int resMsg) {
        this.resMsg = resMsg;
    }
}
