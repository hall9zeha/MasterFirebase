package com.barryzea.inventariofirebase.MainModule.model;

import com.barryzea.inventariofirebase.MainModule.events.MainEvent;
import com.barryzea.inventariofirebase.MainModule.model.dataAccess.ProductEventListener;
import com.barryzea.inventariofirebase.MainModule.model.dataAccess.RealtimeDatabase;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;

public class MainInteractorClass  implements MainInteractor{
    private RealtimeDatabase mDataBase;

    public MainInteractorClass() {
       mDataBase= new RealtimeDatabase();
    }

    @Override
    public void subscribeToProducts() {
        mDataBase.subscribeToProductsListener(new ProductEventListener() {
            @Override
            public void onChildAdded(Product product) {
                post(product, MainEvent.SUCCESS_ADD);
            }

            @Override
            public void onChildUpdate(Product product) {
                post(product, MainEvent.SUCCESS_UPDATE);
            }

            @Override
            public void onChildRemove(Product product) {
                post(product, MainEvent.SUCCESS_REMOVE);
            }

            @Override
            public void onError(int Msg) {
                post(MainEvent.ERROR_SERVER, Msg);
            }
        });
    }

    @Override
    public void unsubscribeToProducts() {
        mDataBase.unsubscribeToProduct();
    }

    @Override
    public void removeProduct(Product product) {
        mDataBase.removeProduct(product, new BasicErrorCallback() {
            @Override
            public void onSuccess() {
                post(MainEvent.SUCCESS_REMOVE);
            }

            @Override
            public void onError(int typeEvent, int resMsg) {
                post(typeEvent, resMsg);
            }
        });
    }

    private void post(int typeEvent){
        post(null, typeEvent, 0);
    }

    private void post(int typeEvent, int resMsg){
        post(null, typeEvent, resMsg);
    }

    private void post(Product product, int typeEvent){
        post(product, typeEvent, 0);
    }
    private void post(Product product, int typeEvent, int resMsg) {
        MainEvent event= new MainEvent();
        event.setProduct(product);
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }
}
