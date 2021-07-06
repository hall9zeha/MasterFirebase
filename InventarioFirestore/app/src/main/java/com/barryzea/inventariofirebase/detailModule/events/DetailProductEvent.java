package com.barryzea.inventariofirebase.detailModule.events;

public class DetailProductEvent {
    public final static int UPDATE_SUCCESS=0;
    public final static int ERROR_SERVER=100;

    private int typeEvent;

    public DetailProductEvent() {
    }

    public int getTypeEvent() {
        return typeEvent;
    }

    public void setTypeEvent(int typeEvent) {
        this.typeEvent = typeEvent;
    }
}
