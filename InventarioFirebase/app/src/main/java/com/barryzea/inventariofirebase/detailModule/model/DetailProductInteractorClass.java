package com.barryzea.inventariofirebase.detailModule.model;

import com.barryzea.inventariofirebase.common.BasicCallbackAddProduct;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.detailModule.events.DetailProductEvent;
import com.barryzea.inventariofirebase.detailModule.model.dataAccess.RealtimeDatabaseDetail;

import org.greenrobot.eventbus.EventBus;

public class DetailProductInteractorClass  implements DetailProductInteractor{
    private RealtimeDatabaseDetail mDataBase;

    public DetailProductInteractorClass() {
        mDataBase = new RealtimeDatabaseDetail();
    }

    @Override
    public void updateProduct(Product product) {
        mDataBase.updateProduct(product, new BasicCallbackAddProduct(){
            @Override
            public void onSuccess() {
                post(DetailProductEvent.UPDATE_SUCCESS);
            }

            @Override
            public void onError() {
                post(DetailProductEvent.ERROR_SERVER);
            }
        });
    }

    private void post(int typeEvent) {
        DetailProductEvent event= new DetailProductEvent();
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }
}
