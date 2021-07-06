package com.barryzea.inventariofirebase.addModule.model;

import com.barryzea.inventariofirebase.addModule.events.AddProductEvent;
import com.barryzea.inventariofirebase.addModule.model.dataAccess.FirebaseFirestoreAdd;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;

public class AddProductInteractorClass implements AddProductInteractor {
    private FirebaseFirestoreAdd mDataBase;

    public AddProductInteractorClass() {
        mDataBase= new FirebaseFirestoreAdd();
    }

    @Override
    public void addProduct(Product product) {
        mDataBase.addProduct(product, new BasicErrorCallback() {
            @Override
            public void onSuccess() {
                post(AddProductEvent.SUCCESS_ADD);
            }

            @Override
            public void onError(int typeEvent, int resMsg) {
                post(typeEvent, resMsg);
            }
        });
    }

    private void post(int typeEvent) {
        post(typeEvent, 0);
    }

    private void post(int typeEvent, int resMsg) {
        AddProductEvent event = new AddProductEvent();
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }
}
